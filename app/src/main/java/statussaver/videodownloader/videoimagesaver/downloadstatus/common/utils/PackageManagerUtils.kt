package statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils

import android.content.Context
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.*

object PackageManagerUtils {

    const val WHATSAPP_PACKAGE_NAME = "com.whatsapp"
    const val WHATSAPP_BUSINESS_PACKAGE_NAME = "com.whatsapp.w4b"

    fun getPackageName(provider: StatusProvider): String? {
        return when (provider) {
            WHATSAPP -> WHATSAPP_PACKAGE_NAME
            WHATSAPP_BUSINESS -> WHATSAPP_BUSINESS_PACKAGE_NAME
            else -> null
        }
    }

    fun getInstalledStatusProviders(context: Context): List<StatusProvider> {
        return listOf(
            WHATSAPP_PACKAGE_NAME to WHATSAPP,
            WHATSAPP_BUSINESS_PACKAGE_NAME to WHATSAPP_BUSINESS,
        )
            .filter { isAppInstalled(context, it.first) }
            .map { it.second }
            .ifEmpty { listOf(WHATSAPP) }
    }

    fun isAppInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager

        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            applicationInfo != null
        } catch (e: Exception) {
            false
        }
    }

}