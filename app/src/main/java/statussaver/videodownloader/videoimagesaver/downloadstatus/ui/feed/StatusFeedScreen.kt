package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.feed

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.statussaver.permission.PermissionState
import com.statussaver.permission.isDenied
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.LoadingScreen
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogHowToUse
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberBottomSheetDismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.IntentUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusResult
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusResult.Loading
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusResult.Success
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.feed.components.EmptyStatusFeed
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.feed.components.StatusFeed
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.feed.components.StatusPermissionFeed

@Composable
fun WaStatusFeedScreen(
    result: StatusResult,
    permissionState: PermissionState,
    storageDeniedPermanently: Boolean,
    isSaving: (Status) -> Boolean,
    onStatusClick: (Status) -> Unit,
    onSaveClick: (Status) -> Unit,
) {
    val context = LocalContext.current

    if (permissionState.status.isDenied) {
        StatusPermissionFeed(
            permission = permissionState,
            storageDeniedPermanently = storageDeniedPermanently
        )
    } else {
        StatusFeedScreen(
            result = result,
            onStatusClick = onStatusClick,
            onSaveClick = onSaveClick,
            isSaving = isSaving,
            emptyFeed = {
                EmptyStatusFeed(
                    illustration = R.drawable.ils_how_it_works_1,
                    text = stringResource(R.string.view_status),
                    actionButtonText = stringResource(R.string.view_status),
                    onActionClick = {
                        IntentUtils.openInstalledWhatsapp(context)
                    },
                )
            }
        )
    }
}

@Composable
fun SavedStatusFeedScreen(
    result: StatusResult,
    onStatusClick: (Status) -> Unit,
    onSaveClick: (Status) -> Unit,
) {
    val howToUseDialog = rememberBottomSheetDismissibleState()

    DialogHowToUse(howToUseDialog)

    StatusFeedScreen(
        result = result,
        onStatusClick = onStatusClick,
        onSaveClick = onSaveClick,
        isSaving = { false },
        emptyFeed = {
            EmptyStatusFeed(
                illustration = R.drawable.ils_how_it_works_2,
                text = stringResource(R.string.save_a_status),
                actionButtonText = stringResource(R.string.how_to_use),
                onActionClick = howToUseDialog::show,
            )
        }
    )
}

@Composable
private fun StatusFeedScreen(
    result: StatusResult,
    isSaving: (Status) -> Boolean,
    onStatusClick: (Status) -> Unit,
    onSaveClick: (Status) -> Unit,
    emptyFeed: @Composable () -> Unit,
) {
    when (result) {
        Loading -> {
            LoadingScreen()
        }

        is Success -> {
            if (result.statuses.isEmpty()) {
                emptyFeed()
            } else {
                StatusFeed(
                    statuses = result.statuses,
                    onStatusClick = onStatusClick,
                    onSaveClick = onSaveClick,
                    isSaving = isSaving,
                )
            }
        }
    }
}

