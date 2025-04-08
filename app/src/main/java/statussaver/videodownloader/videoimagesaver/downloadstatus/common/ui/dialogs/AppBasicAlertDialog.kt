package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import android.view.Window
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackDialogViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.DismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dismissed

@Composable
fun AppBasicAlertDialog(
    state: DismissibleState,
    modifier: Modifier = Modifier,
    eventName: String? = null,
    contentPadding: PaddingValues = PaddingValues(top = 24.dp, start = 16.dp, bottom = 16.dp , end = 16.dp),
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    if (state.dismissed) return

    BasicAlertDialog(
        onDismissRequest = state::dismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        val window = getDialogWindow()

        SideEffect {
            window?.setDimAmount(0.75f)
        }

        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(IntrinsicSize.Min)
                .clip(RoundedCornerShape(28.dp))
                .background(colorScheme.surfaceContainer)
                .padding(contentPadding),
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment,
            content = content,
        )
    }

    eventName?.let {
        TrackDialogViewEvent(it)
    }
}

@Composable
fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window
