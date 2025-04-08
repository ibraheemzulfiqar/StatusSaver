package statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import kotlinx.coroutines.delay
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.toast
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.mock.MockData
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.SAVED
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.StatusSaverTheme
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@Composable
fun StatusThumbnail(
    status: Status,
    isSaving: Boolean,
    onStatusClick: (Status) -> Unit,
    onSaveClick: (Status) -> Unit,
    modifier: Modifier = Modifier,
) {
    val inspectionMode = LocalInspectionMode.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colorScheme.surfaceContainer)
            .aspectRatio(4f / 5f)
            .clickable(
                onClick = { onStatusClick(status) }
            ),
        contentAlignment = Alignment.Center,
    ) {

        if (!inspectionMode && !status.isAudio) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                model = status.path,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            ) { builder ->
                builder
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .signature(ObjectKey("${status.path}-${status.dateModified}"))
            }
        }

        if (status.isVideo) {
            AppImage(AppIcons.MediaPlay)
        }

        if (status.isAudio) {
            AppImage(AppIcons.AudioTrack)
        }

        if (status.provider != SAVED) {
            StatusSaveButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                isSaved = status.isSaved,
                isSaving = isSaving,
                onSaveClick = { onSaveClick(status) }
            )
        }
    }
}

@Composable
private fun StatusSaveButton(
    isSaved: Boolean,
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val minLoadingTime = 500.milliseconds

    var savingStartTime by remember { mutableStateOf<Duration?>(null) }

    LaunchedEffect(isSaving) {
        if (isSaving) {
            if (savingStartTime == null) {
                savingStartTime = currentTimeDuration()
            }
        } else {
            savingStartTime?.let { duration ->
                val currentTime = currentTimeDuration()
                val timeElapsed = currentTime - duration

                if (timeElapsed < minLoadingTime) {
                    delay(minLoadingTime - timeElapsed)
                }

                savingStartTime = null
            }
        }
    }

    Crossfade(
        modifier = modifier,
        targetState = savingStartTime != null,
    ) { saving ->
        if (saving) {
            StatusSavingButton()
        } else {
            AppImageButton(
                image = if (isSaved) {
                    AppIcons.DownloadedFilled
                } else {
                    AppIcons.DownloadFilled
                },
                onClick = {
                    if (isSaved) {
                        context.toast(R.string.already_saved)
                    } else {
                        savingStartTime = currentTimeDuration()
                        onSaveClick()
                    }
                }
            )
        }
    }
}


@Composable
private fun StatusSavingButton(
    modifier: Modifier = Modifier
) {
    IconButton(
        modifier = modifier.requiredSize(AppButtonDefaults.IconButtonSize),
        onClick = {},
    ) {
        AppImage(rememberAppIcon(R.drawable.ic_circle_filled))
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = Color.White,
            strokeWidth = 2.dp,
        )
    }
}

private fun currentTimeDuration() = System.currentTimeMillis().milliseconds


@Preview(device = "spec:width=412dp,height=892dp,dpi=440")
@Composable
private fun StatusPrev() {
    StatusSaverTheme {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = MockData.statusList,
            ) {
                StatusThumbnail(
                    status = it,
                    onSaveClick = {},
                    onStatusClick = {},
                    isSaving = false
                )
            }
        }
    }
}