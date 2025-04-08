package statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.runNonFatal

fun Context.toast(@StringRes id: Int) {
    toast(getString(id))
}

fun Context.toast(message: String?) {
    Handler(Looper.getMainLooper()).post {
        runNonFatal("show_toast") {
            Toast.makeText(this, message.toString(), Toast.LENGTH_SHORT).show()
        }
    }
}


fun Context.findActivity(): Activity? {
    var context = this

    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }

    return null
}