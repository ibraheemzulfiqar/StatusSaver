package com.anggrayudi.storage

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Environment
import android.os.Process

// strip-off version of lib "https://github.com/anggrayudi/SimpleStorage" to save size
// original library has a size of 200-300kb

object SimpleStorage {

    @JvmStatic
    val externalStoragePath: String
        get() = Environment.getExternalStorageDirectory().absolutePath

    @JvmStatic
    fun hasStoragePermission(context: Context): Boolean {
        return checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && hasStorageReadPermission(context)
    }

    @JvmStatic
    fun hasStorageReadPermission(context: Context): Boolean {
        return checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    private fun checkSelfPermission(context: Context, permission: String): Int {
        return context.checkPermission(permission, Process.myPid(), Process.myUid())
    }
}