package com.anggrayudi.storage.extension

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import com.anggrayudi.storage.file.DocumentFileCompat
import com.anggrayudi.storage.file.StorageId.PRIMARY
import com.anggrayudi.storage.file.getStorageId
import java.io.File

/**
 * If given [Uri] with path `/tree/primary:Downloads/MyVideo.mp4`, then return `primary`.
 */
fun Uri.getStorageId(context: Context): String {
    val path = path.orEmpty()
    return if (isRawFile) {
        File(path).getStorageId(context)
    } else when {
        isDownloadsDocument || isDocumentsDocument -> PRIMARY
        isExternalStorageDocument -> path.substringBefore(':', "").substringAfterLast('/')
        else -> ""
    }
}


val Uri.isRawFile: Boolean
    get() = scheme == ContentResolver.SCHEME_FILE

val Uri.isDownloadsDocument: Boolean
    get() = authority == DocumentFileCompat.DOWNLOADS_FOLDER_AUTHORITY


val Uri.isDocumentsDocument: Boolean
    get() = isExternalStorageDocument && path?.let { it.startsWith("/tree/home:") || it.startsWith("/document/home:") } == true

val Uri.isExternalStorageDocument: Boolean
    get() = authority == DocumentFileCompat.EXTERNAL_STORAGE_AUTHORITY

val Uri.isTreeDocumentFile: Boolean
    get() = path?.startsWith("/tree/") == true