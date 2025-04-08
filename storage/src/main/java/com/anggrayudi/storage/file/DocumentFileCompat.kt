package com.anggrayudi.storage.file

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorage
import com.anggrayudi.storage.extension.fromTreeUri
import com.anggrayudi.storage.extension.hasParent
import com.anggrayudi.storage.extension.replaceCompletely
import com.anggrayudi.storage.extension.trimFileSeparator
import com.anggrayudi.storage.file.StorageId.DATA
import com.anggrayudi.storage.file.StorageId.HOME
import com.anggrayudi.storage.file.StorageId.PRIMARY
import java.io.File

object DocumentFileCompat {

    const val EXTERNAL_STORAGE_AUTHORITY = "com.android.externalstorage.documents"

    val SD_CARD_STORAGE_ID_REGEX = Regex("[A-Z0-9]{4}-[A-Z0-9]{4}")

    val SD_CARD_STORAGE_PATH_REGEX = Regex("/storage/$SD_CARD_STORAGE_ID_REGEX(.*?)")


    /* File picker for each API version gives the following URIs:
     * API 26 - 27 => content://com.android.providers.downloads.documents/document/22
     * API 28 - 29 => content://com.android.providers.downloads.documents/document/raw%3A%2Fstorage%2Femulated%2F0%2FDownload%2Fscreenshot.jpeg
     * API 30+     => content://com.android.providers.downloads.documents/document/msf%3A42
     */
    const val DOWNLOADS_FOLDER_AUTHORITY = "com.android.providers.downloads.documents"

    const val DOCUMENTS_TREE_URI = "content://$EXTERNAL_STORAGE_AUTHORITY/tree/home%3A"

    const val DOWNLOADS_TREE_URI = "content://$DOWNLOADS_FOLDER_AUTHORITY/tree/downloads"


    @SuppressLint("UseKtx")
    @JvmOverloads
    @JvmStatic
    fun createDocumentUri(storageId: String, basePath: String = ""): Uri =
        Uri.parse("content://$EXTERNAL_STORAGE_AUTHORITY/tree/" + Uri.encode("$storageId:$basePath"))

    @JvmOverloads
    @JvmStatic
    fun fromFile(
        context: Context,
        file: File,
        requiresWriteAccess: Boolean = false,
        considerRawFile: Boolean = true
    ): DocumentFile? {
        return if (file.checkRequirements(context, requiresWriteAccess, considerRawFile)) {
            DocumentFile.fromFile(file)
        } else {
            val basePath = file.getBasePath(context).removeForbiddenCharsFromFilename().trimFileSeparator()
            exploreFile(context, file.getStorageId(context), basePath, requiresWriteAccess, considerRawFile)
                ?: fromSimplePath(context, file.getStorageId(context), basePath, requiresWriteAccess, considerRawFile)
        }
    }

    private fun exploreFile(
        context: Context,
        storageId: String,
        basePath: String,
        requiresWriteAccess: Boolean,
        considerRawFile: Boolean
    ): DocumentFile? {
        val rawFile = File(buildAbsolutePath(context, storageId, basePath))
        if ((considerRawFile || storageId == DATA) && rawFile.canRead() && rawFile.shouldWritable(context, requiresWriteAccess)) {
            return DocumentFile.fromFile(rawFile)
        }
        val file = if (Build.VERSION.SDK_INT == 29 && (storageId == HOME || storageId == PRIMARY && basePath.hasParent(Environment.DIRECTORY_DOCUMENTS))) {
            getRootDocumentFile(context, storageId, requiresWriteAccess, considerRawFile)?.child(context, basePath)
                ?: context.fromTreeUri(Uri.parse(DOCUMENTS_TREE_URI))?.child(context, basePath.substringAfter(Environment.DIRECTORY_DOCUMENTS))
                ?: return null
        } else if (Build.VERSION.SDK_INT < 30) {
            getRootDocumentFile(context, storageId, requiresWriteAccess, considerRawFile)?.child(context, basePath) ?: return null
        } else {
            val directorySequence = getDirectorySequence(basePath).toMutableList()
            val parentTree = ArrayList<String>(directorySequence.size)
            var grantedFile: DocumentFile? = null
            // Find granted file tree.
            // For example, /storage/emulated/0/Music may not granted, but /storage/emulated/0/Music/Pop is granted by user.
            while (directorySequence.isNotEmpty()) {
                directorySequence.removeFirstOrNull()
                    ?.let { parentTree.add(it) }
                val folderTree = parentTree.joinToString(separator = "/")
                try {
                    grantedFile = context.fromTreeUri(createDocumentUri(storageId, folderTree))
                    if (grantedFile?.canRead() == true) break
                } catch (e: SecurityException) {
                    // ignore
                }
            }
            if (grantedFile == null || directorySequence.isEmpty()) {
                grantedFile
            } else {
                val fileTree = directorySequence.joinToString(prefix = "/", separator = "/")
                context.fromTreeUri(Uri.parse(grantedFile.uri.toString() + Uri.encode(fileTree)))
            }
        }
        return file?.takeIf { it.canRead() }
    }

    @JvmOverloads
    @JvmStatic
    fun fromSimplePath(
        context: Context,
        storageId: String = PRIMARY,
        basePath: String = "",
        requiresWriteAccess: Boolean = false,
        considerRawFile: Boolean = true
    ): DocumentFile? {
        if (storageId == DATA) {
            return DocumentFile.fromFile(context.dataDirectory.child(basePath))
        }
        return if (basePath.isEmpty() && storageId != HOME) {
            getRootDocumentFile(context, storageId, requiresWriteAccess, considerRawFile)
        } else {
            val file = exploreFile(context, storageId, basePath, requiresWriteAccess, considerRawFile)
            if (file == null && storageId == PRIMARY && basePath.hasParent(Environment.DIRECTORY_DOWNLOADS)) {
                val downloads = context.fromTreeUri(Uri.parse(DOWNLOADS_TREE_URI))?.takeIf { it.canRead() } ?: return null
                downloads.child(context, basePath.substringAfter('/', ""))
            } else {
                file
            }
        }
    }

    @JvmStatic
    fun buildAbsolutePath(context: Context, storageId: String, basePath: String): String {
        val cleanBasePath = basePath.removeForbiddenCharsFromFilename()
        val rootPath = when (storageId) {
            PRIMARY -> SimpleStorage.externalStoragePath
            DATA -> context.dataDirectory.path
            HOME -> PublicDirectory.DOCUMENTS.absolutePath
            else -> "/storage/$storageId"
        }
        return "$rootPath/$cleanBasePath".trimEnd('/')
    }

    @JvmOverloads
    @JvmStatic
    fun getRootDocumentFile(
        context: Context,
        storageId: String,
        requiresWriteAccess: Boolean = false,
        considerRawFile: Boolean = true
    ): DocumentFile? {
        if (storageId == DATA) {
            return DocumentFile.fromFile(context.dataDirectory)
        }
        val file = if (storageId == HOME) {
            if (Build.VERSION.SDK_INT == 29) {
                context.fromTreeUri(createDocumentUri(PRIMARY))
            } else {
                DocumentFile.fromFile(Environment.getExternalStorageDirectory())
            }
        } else if (considerRawFile) {
            getRootRawFile(context, storageId, requiresWriteAccess)?.let { DocumentFile.fromFile(it) }
                ?: context.fromTreeUri(createDocumentUri(storageId))
        } else {
            context.fromTreeUri(createDocumentUri(storageId))
        }
        return file?.takeIf { it.canRead() && (requiresWriteAccess && it.isWritable(context) || !requiresWriteAccess) }
    }

    @JvmOverloads
    @JvmStatic
    fun getRootRawFile(context: Context, storageId: String, requiresWriteAccess: Boolean = false): File? {
        val rootFile = when (storageId) {
            PRIMARY, HOME -> Environment.getExternalStorageDirectory()
            DATA -> context.dataDirectory
            else -> File("/storage/$storageId")
        }
        return rootFile.takeIf { rootFile.canRead() && (requiresWriteAccess && rootFile.isWritable(context) || !requiresWriteAccess) }
    }

    internal fun String.removeForbiddenCharsFromFilename(): String = replace(":", "_")
        .replaceCompletely("//", "/")

    internal fun getDirectorySequence(path: String) = path.split('/')
        .filterNot { it.isBlank() }
}