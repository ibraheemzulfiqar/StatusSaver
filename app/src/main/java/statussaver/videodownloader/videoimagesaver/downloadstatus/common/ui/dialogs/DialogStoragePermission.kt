package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.statussaver.permission.PermissionState
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.LocalAnalyticsHelper
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackDialogViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.logEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.toast
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.DismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dismissed
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.IntentUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.runNonFatal
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.VerticalSpacer
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons

@Composable
fun DialogStoragePermission(
    state: DismissibleState,
    permissionState: PermissionState,
    storageDeniedPermanently: Boolean,
) {
    if (state.dismissed) return

    val accentColor = Color(0xFF1EBEA6)
    val containerColor = Color.White
    val contentColor = Color.Black

    val context = LocalContext.current
    val analytics = LocalAnalyticsHelper.current

    BasicAlertDialog(
        onDismissRequest = state::dismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(8.dp))
                .background(containerColor)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(accentColor),
                contentAlignment = Alignment.Center,
            ) {
                AppIcon(
                    icon = AppIcons.Folder,
                    tint = containerColor,
                )
            }

            VerticalSpacer(24.dp)

            Text(
                text = stringResource(R.string.Dialog_legacy_storage_body),
                color = contentColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier.padding(horizontal = 24.dp),
            )

            if (storageDeniedPermanently) {
                VerticalSpacer(12.dp)

                Text(
                    text = stringResource(R.string.Dialog_legacy_storage_body_2),
                    color = contentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    fontFamily = FontFamily.SansSerif,
                    modifier = Modifier.padding(horizontal = 24.dp),
                )
            }

            VerticalSpacer(16.dp)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
            ) {
                StorageButton(
                    text = stringResource(R.string.Dialog_legacy_storage_not_now),
                    color = accentColor,
                    onClick = {
                        state.dismiss()
                        analytics.logEvent("storage_permission_dialog", "action" to "NotNow")
                    }
                )

                StorageButton(
                    text = stringResource(R.string.Dialog_legacy_storage_continue),
                    color = accentColor,
                    onClick = {
                        runNonFatal("storage_permission_dialog") {
                            state.dismiss()

                            if (storageDeniedPermanently) {
                                IntentUtils.openAppSettings(context)
                            } else {
                                permissionState.launchRequest()
                            }
                        }.onFailure {
                            context.toast(R.string.something_went_wrong)
                        }

                        analytics.logEvent(
                            name = "storage_permission_dialog",
                            "action" to "Continue",
                            "canAskForPermission" to storageDeniedPermanently.not(),
                        )
                    }
                )
            }

            VerticalSpacer(16.dp)
        }
    }

    TrackDialogViewEvent("StoragePermission")
}

@Composable
private fun StorageButton(
    text: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
            .padding(8.dp),
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = color,
            fontWeight = FontWeight.Medium,
            fontFamily = FontFamily.SansSerif
        )
    }
}