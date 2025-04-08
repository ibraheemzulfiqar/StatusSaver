package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.LocalAnalyticsHelper
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.logEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.DismissibleState

@Composable
fun DialogDeleteStatus(
    state: DismissibleState,
    onDelete: () -> Unit,
) {
    val analytics = LocalAnalyticsHelper.current

    AppAlertDialog(
        state = state,
        isErrorDialog = true,
        eventName = "Delete",
        title = stringResource(R.string.delete),
        body = stringResource(R.string.are_you_sure_you_want_to_delete_the_status_permanently),
        positionActionText = stringResource(R.string.delete),
        negativeActionText = stringResource(R.string.cancel),
        onNegativeActionClick = {
            state.dismiss()
            analytics.logEvent(name = "delete_dialog", "action" to "Dismiss")
        },
        onPositionActionClick = {
            state.dismiss()
            onDelete()

            analytics.logEvent(name = "delete_dialog", "action" to "Delete")
        }
    )
}