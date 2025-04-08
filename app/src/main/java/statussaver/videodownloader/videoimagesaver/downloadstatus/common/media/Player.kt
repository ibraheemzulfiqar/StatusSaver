@file:OptIn(UnstableApi::class)

package statussaver.videodownloader.videoimagesaver.downloadstatus.common.media

import android.content.Context
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer


@Composable
fun rememberExoPlayerManager(path: String): ExoPlayerManager {
    val context = LocalContext.current
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val manager = remember { ExoPlayerManager(context, path) }

    DisposableEffect(lifecycle) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME, Lifecycle.Event.ON_CREATE -> {
                    manager.initialize()
                }

                Lifecycle.Event.ON_STOP -> {
                    manager.release()
                }

                else -> {}
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            manager.release()
            lifecycle.removeObserver(observer)
        }
    }

    return manager
}


@Immutable
data class PlayerData(
    val mediaId: String,
    val index: Int,
    val position: Long,
    val wasPlaying: Boolean,
)

@Stable
class ExoPlayerManager(
    private val context: Context,
    private val path: String,
) : PlayerManager {

    override var playerData: PlayerData? by mutableStateOf<PlayerData?>(null)
        private set

    override var player: Player? by mutableStateOf<Player?>(null)
        private set

    private val factory = ExoPlayerFactory(path, context)

    private val window: Timeline.Window = Timeline.Window()

    override fun release() {
        player?.let { player ->
            player.currentMediaItem?.let { mediaItem ->
                playerData = PlayerData(
                    mediaId = mediaItem.mediaId,
                    index = player.currentMediaItemIndex,
                    position = player.currentPosition,
                    wasPlaying = player.isPlaying,
                )
            }
            player.release()
        }

        player = null
    }

    override fun initialize() {
        if (player != null) return

        player = factory.createPlayer()

        player?.addListener(object : Player.Listener {
            override fun onTimelineChanged(timeline: Timeline, reason: Int) {
                playerData?.let { data ->
                    if (!timeline.isEmpty
                        && timeline.windowCount > data.index
                        && data.mediaId == timeline.getWindow(
                            data.index,
                            window
                        ).mediaItem.mediaId
                    ) {
                        player?.seekTo(data.index, data.position)
                    }

                }?.also { playerData = null }
            }
        })
    }


}


class ExoPlayerFactory(
    private val path: String,
    private val context: Context,
) : PlayerFactory {

    override fun createPlayer(): Player {
        return ExoPlayer.Builder(context)
            .setSeekBackIncrementMs(5_000L)
            .setSeekForwardIncrementMs(5_000L)
            .build()
            .also { exoPlayer ->
                exoPlayer.trackSelectionParameters = exoPlayer.trackSelectionParameters
                    .buildUpon()
                    .setMaxVideoSizeSd()
                    .build()

                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE

                val mediaItem = MediaItem.fromUri(path)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
            }
    }

}


interface PlayerFactory {
    fun createPlayer(): Player
}

interface PlayerManager {

    val playerData: PlayerData?

    val player: Player?

    fun release()

    fun initialize()

}