package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.media.Media
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.media.MediaState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.media.ShowBuffering
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.media.rememberControllerState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.media.rememberExoPlayerManager
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.media.rememberMediaState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.AppSliderDefaults.SliderColors
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppImage
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.BetterBlack
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.DimenTokens
import java.util.Formatter
import java.util.Locale

@Composable
fun VideoPlayer(
    status: Status,
    modifier: Modifier = Modifier,
    pageVisible: Boolean = true,
) {
    val playerManager = rememberExoPlayerManager(status.path)
    val player = playerManager.player
    val state = rememberMediaState(player)

    val lifecycleOwner = LocalLifecycleOwner.current
    val currentState = lifecycleOwner.lifecycle.currentState

    LaunchedEffect(pageVisible, player, currentState) {
        if (pageVisible && currentState.isAtLeast(Lifecycle.State.STARTED)) {
            playerManager.initialize()

            player?.play()
        } else {
            player?.pause()
        }
    }

    Media(
        modifier = modifier.fillMaxSize(),
        state = state,
        useArtwork = true,
        showBuffering = ShowBuffering.Always,
        buffering = { Buffering() },
        defaultArtworkPainter = AppIcons.AudioTrack.painter,
    ) { mediaState ->
        Crossfade(
            targetState = mediaState.isControllerShowing,
        ) { visible ->
            if (visible) {
                VideoController(
                    mediaState = mediaState,
                    pageVisible = pageVisible,
                )
            }
        }
    }
}

@Composable
fun VideoController(
    mediaState: MediaState,
    modifier: Modifier = Modifier,
    pageVisible: Boolean = true,
) {
    val controller = rememberControllerState(mediaState)
    var positionMs by remember { mutableLongStateOf(controller.getCurrentPosition() ?: 0) }

    val positionAnimation = remember { Animatable(positionMs.toFloat()) }
    val scope = rememberCoroutineScope()

    val positionUpdateDelay = 500L
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(pageVisible) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (isActive && pageVisible) {
                val newPosition = controller.getCurrentPosition()

                if (newPosition == null) {
                    delay(positionUpdateDelay)
                    continue
                }

                positionMs = newPosition

                scope.launch {
                    if (positionMs > positionAnimation.value) {
                        positionAnimation.animateTo(
                            targetValue = positionMs.toFloat(),
                            animationSpec = tween(
                                positionUpdateDelay.toInt(),
                                easing = LinearEasing
                            )
                        )
                    } else {
                        positionAnimation.snapTo(positionMs.toFloat())
                    }
                }

                delay(positionUpdateDelay)
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Spacer(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, BetterBlack)
                    )
                )
                .fillMaxWidth()
                .padding(bottom = DimenTokens.ViewerButtonContainerHeight + 64.dp)
                .navigationBarsPadding()
        )
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(bottom = DimenTokens.ViewerButtonContainerHeight)
                .navigationBarsPadding(),
            contentAlignment = Alignment.Center
        ) {
            PlayPauseButton(
                showPause = controller.showPause,
                onClick = controller::playOrPause,
                modifier = Modifier.align(Alignment.Center)
            )

            AppTimeBar(
                modifier = Modifier.align(Alignment.BottomStart),
                value = positionAnimation.value / controller.durationMs.toFloat(),
                onValueChange = {
                    val newPosition = it * controller.durationMs

                    scope.launch {
                        positionAnimation.snapTo(newPosition)
                        controller.seekTo(newPosition.toLong())
                    }
                },
                durationMs = controller.durationMs,
                positionMs = positionMs,
            )
        }
    }
}

@Composable
fun PlayPauseButton(
    showPause: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val image = if (showPause) {
        AppIcons.MediaPause
    } else {
        AppIcons.MediaPlay
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                role = Role.Button,
                interactionSource = null,
                indication = ripple(),
                onClick = onClick
            ),
        contentAlignment = Alignment.Center,
    ) {
        AppImage(
            image = image,
            modifier = Modifier.size(80.dp)
        )
    }
}

@Composable
fun AppTimeBar(
    value: Float,
    durationMs: Long,
    positionMs: Long,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    colors: SliderColors = AppSliderDefaults.colors(),
) {
    val formatterBuilder = remember { StringBuilder() }
    val formatter = remember { Formatter(formatterBuilder, Locale.ENGLISH) }

    var isDragging by remember { mutableStateOf(false) }

    val progressTrackColor by animateColorAsState(
        targetValue = if (isDragging) colors.trackProgressActive else colors.trackProgress
    )

    @Composable
    fun TimedText(time: Long) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = stringForTime(time.toInt(), formatterBuilder, formatter),
                color = progressTrackColor,
                fontSize = 12.sp,
            )
            Text(
                text = "999:999",
                color = Color.Transparent,
                fontSize = 12.sp
            )
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectDragGestures { _, _ -> }
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        TimedText(positionMs)

        AppSlider(
            modifier = Modifier.weight(1f),
            value = value,
            onValueChange = onValueChange,
            colors = colors,
            onDragStarted = {
                isDragging = true
            },
            onDragStopped = {
                isDragging = false
            }
        )

        TimedText(durationMs)
    }
}

@Composable
fun Buffering(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = DimenTokens.ViewerButtonContainerHeight)
            .navigationBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(Modifier.size(100.dp))
    }
}

private fun stringForTime(
    timeMs: Int,
    formatBuilder: StringBuilder,
    formatter: Formatter,
): String {
    val totalSeconds = timeMs / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    formatBuilder.setLength(0);

    return if (hours > 0) {
        formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
    } else {
        formatter.format("%02d:%02d", minutes, seconds).toString();
    }
}