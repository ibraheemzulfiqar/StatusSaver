package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.feed.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.StatusThumbnail

@Composable
fun StatusFeed(
    statuses: List<Status>,
    onStatusClick: (Status) -> Unit,
    onSaveClick: (Status) -> Unit,
    isSaving: (Status) -> Boolean,
    modifier: Modifier = Modifier,
) {
    val state = rememberLazyGridState()
    var previousSize by rememberSaveable { mutableIntStateOf(statuses.size) }

    LaunchedEffect(statuses.size) {
        if (statuses.size != previousSize) {
            state.scrollToItem(0)
        }

        previousSize = statuses.size
    }

    LazyVerticalGrid(
        state = state,
        modifier = modifier.fillMaxSize(),
        columns = GridCells.Adaptive(150.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp),
    ) {
        items(
            items = statuses,
            key = { it.path },
        ) { status ->

            StatusThumbnail(
                status = status,
                onSaveClick = onSaveClick,
                onStatusClick = onStatusClick,
                isSaving = isSaving(status),
            )
        }
    }
}
