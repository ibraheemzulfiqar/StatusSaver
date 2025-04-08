package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.AppSliderDefaults.SliderColors


@Composable
fun AppSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    trackHeight: Dp = 12.dp,
    heightChangeFactor: Float = 1.5f,
    colors: SliderColors = AppSliderDefaults.colors(),
    onDragStarted: () -> Unit = {},
    onDragStopped: () -> Unit = {},
) {
    var width by remember { mutableFloatStateOf(0f) }
    var isDragging by remember { mutableStateOf(false) }

    val progressTrackColor by animateColorAsState(
        targetValue = if (isDragging) colors.trackProgressActive else colors.trackProgress
    )

    val height by animateDpAsState(
        targetValue = if (isDragging) trackHeight * heightChangeFactor else trackHeight
    )

    val cornerRadius by remember(height) { mutableStateOf(height / 2) }

    Canvas(
        modifier = modifier
            .height(height)
            .onSizeChanged {
                width = it.width.toFloat()
            }
            .draggable(
                state = rememberDraggableState { delta ->
                    if (width > 0) {
                        val newValue = (value + (delta / width)).coerceIn(0f, 1f)
                        onValueChange(newValue)
                    }
                },
                orientation = Orientation.Horizontal,
                startDragImmediately = true,
                onDragStarted = {
                    isDragging = true
                    onDragStarted()
                },
                onDragStopped = {
                    isDragging = false
                    onDragStopped()
                }
            )
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawRoundRect(
            color = colors.track,
            topLeft = Offset.Zero,
            size = Size(canvasWidth, canvasHeight),
            cornerRadius = CornerRadius(cornerRadius.toPx())
        )

        val progressWidth = canvasWidth * value

        val clipPath = Path().apply {
            addRoundRect(
                RoundRect(
                    rect = Rect(Offset.Zero, Size(canvasWidth, canvasHeight)),
                    cornerRadius = CornerRadius(cornerRadius.toPx())
                )
            )
        }

        clipPath(clipPath) {
            drawRect(
                color = progressTrackColor,
                topLeft = Offset.Zero,
                size = Size(progressWidth, canvasHeight)
            )
        }
    }

}

object AppSliderDefaults {

    fun colors() = SliderColors(
        track = Color(0xFF3B393C),
        trackProgress = Color(0xFFDEDEDE),
//        trackProgress = Color(0xFFA7A7AE),
        trackProgressActive = Color(0xFFFFFFFF),
    )

    data class SliderColors(
        val track: Color,
        val trackProgress: Color,
        val trackProgressActive: Color,
    )
}