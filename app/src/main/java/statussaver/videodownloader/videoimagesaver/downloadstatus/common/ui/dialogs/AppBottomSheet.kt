package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.runtime.Composable
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackDialogViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.BottomSheetDismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dismissed

@Composable
fun AppBottomSheet(
    state: BottomSheetDismissibleState,
    eventName: String,
    dragHandle: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    if (state.dismissed) return

    ModalBottomSheet(
        onDismissRequest = state::dismiss,
        sheetState = state.sheetState,
        containerColor = colorScheme.surfaceContainer,
        contentColor = colorScheme.onSurface,
        dragHandle = dragHandle,
        content = content,
    )

    TrackDialogViewEvent(eventName)
}