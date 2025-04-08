package com.anggrayudi.storage.file

import android.content.Context
import android.os.Environment

object StorageId {

    /**
     * For files under [Environment.getExternalStorageDirectory]
     */
    const val PRIMARY = "primary"

    /**
     * For files under [Context.getFilesDir] or [Context.getDataDir].
     * It is not really a storage ID, and can't be used in file tree URI.
     */
    const val DATA = "data"

    /**
     * For `/storage/emulated/0/Documents`
     * It is only exists on API 29-
     */
    const val HOME = "home"
}