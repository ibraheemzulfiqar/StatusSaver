package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.statussaver.permission.PermissionState
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.toast
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.BottomSheetDismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.UseThisFolderGuideText
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.runNonFatal
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButton
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppImage
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.permissionoverly.PermissionOverlyActivity

@Composable
fun DialogFolderPermission(
    state: BottomSheetDismissibleState,
    permissionState: PermissionState,
) {
    val context = LocalContext.current
    val configuration = LocalConfiguration.current

    AppBottomSheet(
        state = state,
        eventName = "FolderPermission"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text(
                text = stringResource(R.string.permission_required),
                style = typography.headlineLarge
            )

            if (configuration.screenHeightDp >= 480) {
                AppImage(
                    image = AppIcons.UseThisFolder,
                    modifier = Modifier
                        .sizeIn(maxWidth = 380.dp)
                        .aspectRatio(2f),
                )
            }

            UseThisFolderGuideText(Modifier.padding(horizontal = 32.dp))

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.allow),
                onClick = {
                    runNonFatal("folder_permission_dialog") {
                        state.dismiss()
                        permissionState.launchRequest()
                        PermissionOverlyActivity.start(context)
                    }.onFailure {
                        context.toast(R.string.something_went_wrong)
                    }
                }
            )
        }
    }
}