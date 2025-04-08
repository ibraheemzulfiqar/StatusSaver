package com.anggrayudi.storage.file

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.Environment.isExternalStorageManager
import androidx.annotation.RestrictTo
import com.anggrayudi.storage.SimpleStorage
import com.anggrayudi.storage.extension.trimFileSeparator
import com.anggrayudi.storage.file.StorageId.DATA
import com.anggrayudi.storage.file.StorageId.PRIMARY
import java.io.File
import java.io.IOException

/**
 * @see [Context.getDataDir]
 * @see [Context.getFilesDir]
 */
val Context.dataDirectory: File
    get() = if (Build.VERSION.SDK_INT > 23) dataDir else filesDir.parentFile!!

/**
 * ID of this storage. For external storage, it will return [PRIMARY],
 * otherwise it is a SD Card and will return integers like `6881-2249`.
 */
fun File.getStorageId(context: Context) = when {
    path.startsWith(SimpleStorage.externalStoragePath) -> PRIMARY
    path.startsWith(context.dataDirectory.path) -> DATA
    else -> if (path.matches(DocumentFileCompat.SD_CARD_STORAGE_PATH_REGEX)) {
        path.substringAfter("/storage/", "").substringBefore('/')
    } else ""
}

fun File.getBasePath(context: Context): String {
    val externalStoragePath = SimpleStorage.externalStoragePath
    if (path.startsWith(externalStoragePath)) {
        return path.substringAfter(externalStoragePath, "").trimFileSeparator()
    }
    val dataDir = context.dataDirectory.path
    if (path.startsWith(dataDir)) {
        return path.substringAfter(dataDir, "").trimFileSeparator()
    }
    val storageId = getStorageId(context)
    return path.substringAfter("/storage/$storageId", "").trimFileSeparator()
}

@RestrictTo(RestrictTo.Scope.LIBRARY)
fun File.shouldWritable(context: Context, requiresWriteAccess: Boolean) = requiresWriteAccess && isWritable(context) || !requiresWriteAccess


@RestrictTo(RestrictTo.Scope.LIBRARY)
fun File.checkRequirements(context: Context, requiresWriteAccess: Boolean, considerRawFile: Boolean) = canRead() && shouldWritable(context, requiresWriteAccess)
        && (considerRawFile || isExternalStorageManager(context))

/**
 * Use it, because [File.canWrite] is unreliable on Android 10.
 * Read [this issue](https://github.com/anggrayudi/SimpleStorage/issues/24#issuecomment-830000378)
 */
fun File.isWritable(context: Context) = canWrite() && (isFile || isExternalStorageManager(context))

/**
 * @return `true` if you have full disk access
 * @see Environment.isExternalStorageManager
 */
fun File.isExternalStorageManager(context: Context) = Build.VERSION.SDK_INT > Build.VERSION_CODES.Q && Environment.isExternalStorageManager(this)
        || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q && path.startsWith(SimpleStorage.externalStoragePath) && SimpleStorage.hasStoragePermission(context)
        || context.writableDirs.any { path.startsWith(it.path) }

/**
 * These directories do not require storage permissions. They are always writable with full disk access.
 */
val Context.writableDirs: Set<File>
    get() {
        val dirs = mutableSetOf(dataDirectory)
        dirs.addAll(obbDirs.filterNotNull())
        dirs.addAll(getExternalFilesDirs(null).mapNotNull { it?.parentFile })
        return dirs
    }

fun File.child(path: String) = File(this, path)
