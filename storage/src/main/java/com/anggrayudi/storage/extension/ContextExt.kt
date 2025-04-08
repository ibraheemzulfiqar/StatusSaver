package com.anggrayudi.storage.extension

import android.content.Context
import android.net.Uri
import androidx.documentfile.provider.DocumentFile

fun Context.fromTreeUri(fileUri: Uri) = try {
    DocumentFile.fromTreeUri(this, fileUri)
} catch (e: Exception) {
    null
}