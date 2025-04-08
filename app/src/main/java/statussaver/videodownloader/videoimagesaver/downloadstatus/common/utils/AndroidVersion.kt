package statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils

import android.os.Build

fun isAndroidMin11() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R
fun isAndroidBelow11() = Build.VERSION.SDK_INT < Build.VERSION_CODES.R
fun isAndroidMin13() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
fun isAndroidBelow13() = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
fun isAndroidOn10() = Build.VERSION.SDK_INT == Build.VERSION_CODES.Q
