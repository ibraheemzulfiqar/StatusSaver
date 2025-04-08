package statussaver.videodownloader.videoimagesaver.downloadstatus.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import timber.log.Timber

class RestartReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Timber.tag("RestartReceiver").e("onReceive, ${intent?.action}")

        val action = intent?.action

        if (context == null) return

        if (action == "android.intent.action.BOOT_COMPLETED" ||
            action == "android.intent.action.LOCKED_BOOT_COMPLETED" ||
            action == "android.intent.action.REBOOT" ||
            action == "android.intent.action.QUICKBOOT_POWERON" ||
            action == "com.htc.intent.action.QUICKBOOT_POWERON"
        ) {
            NotifyStatusService.start(context)
        }
    }

}