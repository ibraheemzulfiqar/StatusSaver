package statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.AppDetails
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.PackageManagerUtils.getInstalledStatusProviders
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.PackageManagerUtils.getPackageName
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType.AUDIO
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType.IMAGE
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.MediaType.VIDEO
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import java.io.File
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

object IntentUtils {

    fun openAppSettings(context: Context) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
        context.startActivity(intent)
    }

    fun openInstalledWhatsapp(context: Context) {
        val providers = getInstalledStatusProviders(context)
        val packageName = getPackageName(providers[0])

        openApp(context, packageName)
    }

    fun sendDirectMessage(
        context: Context,
        number: String,
        message: String,
        packageName: String,
    ) {
        runNonFatal("open_direct_message") {
            val message = URLEncoder.encode(
                message.trimIndent(),
                StandardCharsets.UTF_8.toString()
            )
            val url = StringBuilder().apply {
                append("https://wa.me/")
                append(number)
                if (message.isNotEmpty()) {
                    append("/?text=$message")
                }
            }.toString()

            openUrl(
                context = context,
                url = url,
                applicationId = packageName,
            )
        }
    }

    fun repostStatus(
        context: Context,
        status: Status,
    ) {
        val installedWhatsapp = getInstalledStatusProviders(context)
            .firstOrNull()
            ?.let { getPackageName(it) }

        shareStatus(
            context = context,
            status = status,
            appPackageName = installedWhatsapp,
        )
    }

    fun shareStatus(
        context: Context,
        status: Status,
        appPackageName: String? = null,
    ) {
        val mimeType = when (status.type) {
            IMAGE -> "image/*"
            VIDEO -> "video/*"
            AUDIO -> "audio/*"
        }

        shareMedia(
            context = context,
            path = status.path,
            mimeType = mimeType,
            appPackageName = appPackageName,
        )
    }

    fun shareMedia(
        context: Context,
        path: String,
        mimeType: String,
        appPackageName: String? = null,
    ) {
        val contentUri = if (path.startsWith(Environment.getExternalStorageDirectory().path)) {
            FileProvider.getUriForFile(context, context.packageName, File(path))
        } else {
            path.toUri()
        }

        context.grantUriPermission(
            context.packageName,
            contentUri,
            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
        )

        val intent = Intent(Intent.ACTION_SEND).apply {
            putExtra(Intent.EXTRA_STREAM, contentUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            type = mimeType

            if (appPackageName != null) {
                setPackage(appPackageName)
            }
        }
        context.startActivity(Intent.createChooser(intent, null))
    }

    fun openApp(context: Context?, applicationId: String?) {
        if (context == null || applicationId == null) return

        runNonFatal("open_app_(${applicationId})") {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(applicationId)
            context.startActivity(launchIntent)
        }.onFailure {
            val url = "https://play.google.com/store/apps/details?id=$applicationId"
            browseUrl(context, url)
        }
    }

    fun openUrl(
        context: Context?,
        applicationId: String,
        url: String,
    ) {
        if (context == null) return

        runNonFatal("open_url") {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = url.toUri()
                setPackage(applicationId)
            }

            context.startActivity(sendIntent)
        }.onFailure {
            browseUrl(context, url)
        }
    }

    fun browseUrl(
        context: Context?,
        url: String,
        newTask: Boolean = false,
    ): Boolean {
        return runNonFatal("browse_url") {
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            if (newTask) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context?.startActivity(intent)
            true
        }.getOrDefault(false)
    }

    fun openNotificationSettings(
        packageName: String,
        launcher: ActivityResultLauncher<Intent>,
    ) {
        runNonFatal("notification_settings") {
            val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
            }
            launcher.launch(intent)
        }.onFailure {
            runNonFatal("notification_settings_app_details") {
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                launcher.launch(intent)
            }
        }
    }

    fun sendMail(
        context: Context,
        address: String = AppDetails.FEEDBACK_EMAIL,
        subject: String = AppDetails.FEEDBACK_EMAIL_SUBJECT,
        message: String = "",
    ) {
        runNonFatal("send_mail") {
            val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                this.type = "*/*"

                setPackage(AppDetails.DEFAULT_EMAIL_APP)

                putExtra(Intent.EXTRA_EMAIL, arrayOf(address))
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
            context.startActivity(intent)
        }
    }
}