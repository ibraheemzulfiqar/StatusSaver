package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackDialogViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.VerticalSpacer

@Composable
fun DialogEnhanceProgress(progress: Int) {
    val progressAnimated by animateIntAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = "progress"
    )

    ModalBottomSheet(
        onDismissRequest = { },
        properties = ModalBottomSheetProperties(shouldDismissOnBackPress = false),
        sheetState = rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = { false },
        ),
        containerColor = colorScheme.surfaceContainer,
        contentColor = colorScheme.onSurface,
        dragHandle = {},
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .drawBehind {
                    val width = size.width * (progressAnimated.toFloat() / 100f)

                    drawRect(
                        color = Color.White.copy(alpha = 0.25f),
                        size = Size(width, size.height)
                    )
                }
                .padding(24.dp),
        ) {
            Text(
                text = "${progressAnimated}%",
                style = typography.headlineLarge,
            )

            VerticalSpacer(16.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    color = colorScheme.onSurface,
                )

                Text(
                    text = stringResource(R.string.enhancing),
                    style = typography.headlineLarge,
                )
            }

            VerticalSpacer(16.dp)

            Text(
                text = stringResource(R.string.it_may_take_few_seconds_to_enhance_the_image),
                style = typography.bodyMedium,
                color = colorScheme.onSurfaceVariant,
            )

            VerticalSpacer(32.dp)
        }
    }

    TrackDialogViewEvent("EnhanceProgress")
}