package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.feed.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.statussaver.permission.PermissionState
import statussaver.videodownloader.videoimagesaver.downloadstatus.R.string
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.toast
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.StoragePermissionGuideText
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.StoragePermissionSettingsGuideText
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.UseThisFolderGuideText
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.IntentUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.isAndroidBelow11
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.isAndroidMin11
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.runNonFatal
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButton
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.permissionoverly.PermissionOverlyActivity


@Composable
fun StatusPermissionFeed(
    permission: PermissionState,
    storageDeniedPermanently: Boolean,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(
        LocalContentColor provides colorScheme.onSurface,
        LocalTextStyle provides typography.bodyMedium,
    ) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(colorScheme.surface)
                .padding(16.dp),
        ) {
            WarningRow()

            Spacer(Modifier.weight(1f))

            Column(Modifier.padding(horizontal = 32.dp)) {
                Text(stringResource(string.Permission_to_view_status))

                Spacer(Modifier.height(16.dp))

                if (isAndroidMin11()) {
                    UseThisFolderGuideText()
                } else {
                    if (storageDeniedPermanently) {
                        StoragePermissionSettingsGuideText()
                    } else {
                        StoragePermissionGuideText()
                    }
                }

                Spacer(Modifier.height(24.dp))

                PermissionButton(
                    permission = permission,
                    storageDeniedPermanently = storageDeniedPermanently,
                )
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
private fun PermissionButton(
    permission: PermissionState,
    storageDeniedPermanently: Boolean,
) {
    val context = LocalContext.current
    val openSettings = isAndroidBelow11() && storageDeniedPermanently

    val text = if (openSettings) {
        stringResource(string.settings)
    } else {
        stringResource(string.Permission_allow)
    }

    AppButton(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        onClick = {
            runNonFatal("permission_request") {
                if (openSettings) {
                    IntentUtils.openAppSettings(context)
                } else {
                    permission.launchRequest()

                    if (isAndroidMin11()) {
                        PermissionOverlyActivity.start(context)
                    }
                }
            }.onFailure {
                context.toast(string.something_went_wrong)
            }
        },
    )
}

@Composable
private fun WarningRow() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colorScheme.surfaceContainer)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIcon(
            modifier = Modifier.size(16.dp),
            icon = AppIcons.Warning,
            tint = colorScheme.onSurfaceVariant
        )
        Text(
            text = stringResource(string.Permission_warning_text),
            color = colorScheme.onSurface,
            style = typography.bodySmall,
        )
    }
}