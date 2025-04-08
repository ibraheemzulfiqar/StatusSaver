package statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils

import android.os.Handler
import android.os.Looper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

fun ensureMainThread(block: () -> Unit) {
    if (Thread.currentThread() == Looper.getMainLooper()) {
        block()
    } else {
        Handler(Looper.getMainLooper()).post {
            block()
        }
    }
}

