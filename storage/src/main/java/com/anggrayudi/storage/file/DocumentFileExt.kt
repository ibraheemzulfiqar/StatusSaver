package com.anggrayudi.storage.file

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import androidx.annotation.RequiresApi
import androidx.annotation.RestrictTo
import androidx.documentfile.provider.DocumentFile
import com.anggrayudi.storage.SimpleStorage
import com.anggrayudi.storage.extension.fromTreeUri
import com.anggrayudi.storage.extension.getStorageId
import com.anggrayudi.storage.extension.isDocumentsDocument
import com.anggrayudi.storage.extension.isDownloadsDocument
import com.anggrayudi.storage.extension.isExternalStorageDocument
import com.anggrayudi.storage.extension.isRawFile
import com.anggrayudi.storage.extension.isTreeDocumentFile
import com.anggrayudi.storage.extension.trimFileSeparator
import com.anggrayudi.storage.file.StorageId.PRIMARY
import java.io.File

val DocumentFile.isTreeDocumentFile: Boolean
    get() = uri.isTreeDocumentFile

val DocumentFile.isRawFile: Boolean
    get() = uri.isRawFile

val DocumentFile.isExternalStorageDocument: Boolean
    get() = uri.isExternalStorageDocument

val DocumentFile.isDownloadsDocument: Boolean
    get() = uri.isDownloadsDocument

val DocumentFile.isDocumentsDocument: Boolean
    get() = uri.isDocumentsDocument

val DocumentFile.id: String
    get() = DocumentsContract.getDocumentId(uri)

val DocumentFile.rootId: String
    get() = DocumentsContract.getRootId(uri)

@RequiresApi(Build.VERSION_CODES.R)
fun DocumentFile.getBasePath(context: Context): String {
    val path = uri.path.orEmpty()
    val storageID = getStorageId(context)
    return when {
        isRawFile -> File(path).getBasePath(context)

        isDocumentsDocument -> {
            "${Environment.DIRECTORY_DOCUMENTS}/${path.substringAfterLast("/home:", "")}".trimEnd('/')
        }

        isExternalStorageDocument && path.contains("/document/$storageID:") -> {
            path.substringAfterLast("/document/$storageID:", "").trimFileSeparator()
        }

        isDownloadsDocument -> {
            when {
                path.matches(Regex("(.*?)/ms[f,d]:\\d+(.*?)")) -> {
                    if (isTreeDocumentFile) {
                        val parentTree = mutableListOf(name.orEmpty())
                        var parent = this
                        while (parent.parentFile?.also { parent = it } != null) {
                            parentTree.add(parent.name.orEmpty())
                        }
                        parentTree.reversed().joinToString("/")
                    } else {
                        // we can't use msf/msd ID as MediaFile ID to fetch relative path, so just return empty String
                        ""
                    }
                }

                else -> path.substringAfterLast(SimpleStorage.externalStoragePath, "").trimFileSeparator()
            }
        }

        else -> ""
    }
}

/**
 * ID of this storage. For external storage, it will return [PRIMARY],
 * otherwise it is a SD Card and will return integers like `6881-2249`.
 * However, it will return empty `String` if this [DocumentFile] is picked from [Intent.ACTION_OPEN_DOCUMENT] or [Intent.ACTION_CREATE_DOCUMENT]
 */
fun DocumentFile.getStorageId(context: Context) = uri.getStorageId(context)

/**
 * Use it, because [DocumentFile.canWrite] is unreliable on Android 10.
 * Read [this issue](https://github.com/anggrayudi/SimpleStorage/issues/24#issuecomment-830000378)
 */
fun DocumentFile.isWritable(context: Context) = if (isRawFile) File(uri.path!!).isWritable(context) else canWrite()

@RestrictTo(RestrictTo.Scope.LIBRARY)
fun DocumentFile.shouldWritable(context: Context, requiresWriteAccess: Boolean) = requiresWriteAccess && isWritable(context) || !requiresWriteAccess

@RestrictTo(RestrictTo.Scope.LIBRARY)
fun DocumentFile.takeIfWritable(context: Context, requiresWriteAccess: Boolean) = takeIf { it.shouldWritable(context, requiresWriteAccess) }

@JvmOverloads
fun DocumentFile.child(context: Context, path: String, requiresWriteAccess: Boolean = false): DocumentFile? {
    return when {
        path.isEmpty() -> this
        isDirectory -> {
            val file = if (isRawFile) {
                quickFindRawFile(path)
            } else {
                var currentDirectory = this
                val resolver = context.contentResolver
                DocumentFileCompat.getDirectorySequence(path).forEach {
                    val directory = currentDirectory.quickFindTreeFile(context, resolver, it) ?: return null
                    if (directory.canRead()) {
                        currentDirectory = directory
                    } else {
                        return null
                    }
                }
                currentDirectory
            }
            file?.takeIfWritable(context, requiresWriteAccess)
        }

        else -> null
    }
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
fun DocumentFile.quickFindRawFile(name: String): DocumentFile? {
    return DocumentFile.fromFile(File(uri.path!!, name)).takeIf { it.canRead() }
}

@SuppressLint("NewApi")
@RestrictTo(RestrictTo.Scope.LIBRARY)
fun DocumentFile.quickFindTreeFile(context: Context, resolver: ContentResolver, name: String): DocumentFile? {
    try {
        // Optimized algorithm. Do not change unless you really know algorithm complexity.
        val childrenUri = DocumentsContract.buildChildDocumentsUriUsingTree(uri, id)
        resolver.query(childrenUri, arrayOf(DocumentsContract.Document.COLUMN_DOCUMENT_ID), null, null, null)?.use {
            val columnName = arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME)
            while (it.moveToNext()) {
                try {
                    val documentUri = DocumentsContract.buildDocumentUriUsingTree(uri, it.getString(0))
                    resolver.query(documentUri, columnName, null, null, null)?.use { childCursor ->
                        if (childCursor.moveToFirst() && name == childCursor.getString(0))
                            return context.fromTreeUri(documentUri)
                    }
                } catch (e: Exception) {
                    // ignore
                }
            }
        }
    } catch (e: Exception) {
        // ignore
    }
    return null
}

@RequiresApi(Build.VERSION_CODES.R)
fun DocumentFile.toRawFile(context: Context): File? {
    return when {
        isRawFile -> File(uri.path ?: return null)
        inPrimaryStorage(context) -> File("${SimpleStorage.externalStoragePath}/${getBasePath(context)}")
        else -> getStorageId(context).let { storageId ->
            if (storageId.isNotEmpty()) {
                File("/storage/$storageId/${getBasePath(context)}")
            } else {
                null
            }
        }
    }
}

fun DocumentFile.inPrimaryStorage(context: Context) = isTreeDocumentFile && getStorageId(context) == PRIMARY
        || isRawFile && uri.path.orEmpty().startsWith(SimpleStorage.externalStoragePath)