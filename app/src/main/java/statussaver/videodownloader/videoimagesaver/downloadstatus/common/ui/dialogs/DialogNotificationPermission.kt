package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import com.statussaver.permission.PermissionState
import com.statussaver.permission.Permissions
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.LocalAnalyticsHelper
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.logEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.findActivity
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.toast
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.DismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.IntentUtils.openNotificationSettings
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.PermissionUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.runNonFatal
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons


@Composable
fun DialogNotificationPermission(
    state: DismissibleState,
    permissionState: PermissionState,
    isPermanentlyDenied: Boolean,
    onResult: (granted: Boolean, canRequestAgain: Boolean) -> Unit,
) {
    val context = LocalContext.current.findActivity()!!
    val analytics = LocalAnalyticsHelper.current

    val settingsLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        val granted = PermissionUtils.hasNotificationPermission(context)
        val canRequestAgain =
            ActivityCompat.shouldShowRequestPermissionRationale(context, Permissions.NOTIFICATION)

        onResult(granted, canRequestAgain)

        analytics.logEvent(
            name = "notification_permission_dialog",
            "granted" to granted,
            "canRequestAgain" to canRequestAgain,
        )
    }


    AppAlertDialog(
        state = state,
        title = stringResource(R.string.Dialog_notification_permission_title),
        body = stringResource(R.string.Dialog_notification_permission_body),
        icon = AppIcons.NotificationPermission,
        eventName = "NotificationPermission",
        positionActionText = stringResource(R.string.allow),
        negativeActionText = stringResource(R.string.later),
        onNegativeActionClick = {
            state.dismiss()
            analytics.logEvent(
                name = "notification_permission_dialog",
                "action" to "Later",
            )
        },
        onPositionActionClick = {
            state.dismiss()

            runNonFatal("notification_permission") {
                if (isPermanentlyDenied) {
                    openNotificationSettings(
                        packageName = context.packageName,
                        launcher = settingsLauncher,
                    )
                } else {
                    permissionState.launchRequest()
                }
            }.onFailure {
                context.toast(R.string.something_went_wrong)
            }

            analytics.logEvent(
                name = "notification_permission_dialog",
                "action" to "Allow",
                "canAskForPermission" to isPermanentlyDenied.not(),
            )
        },
    )
}