package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusRepository
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer.EnhancerState.Finish
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer.EnhancerState.Idle
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer.EnhancerState.Loading
import javax.inject.Inject

@HiltViewModel
class ImageEnhancerViewModel @Inject constructor(
    private val repository: StatusRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val route = savedStateHandle.toRoute<ImageEnhancerRoute>()

    var state: EnhancerState by mutableStateOf(Idle)
        private set

    fun enhanceImage() {
        state = Loading(0)

        viewModelScope.launch {
            val timeBaseProgress = launch {
                var progress = 0

                while (isActive && progress < 90) {
                    state = Loading(progress)

                    progress += if (progress >= 80) 1 else 10
                    delay(1000)
                }
            }

            val path = repository.saveEnhancedStatus(
                path = route.path,
                extension = route.displayName.substringAfterLast(".",""),
            )

            timeBaseProgress.cancelAndJoin()

            state = Loading(100)
            delay(1500)

            state = Finish(path)
        }
    }

}

sealed interface EnhancerState {

    data object Idle : EnhancerState

    data class Loading(
        val progress: Int
    ) : EnhancerState

    data class Finish(
        val path: String?,
    ) : EnhancerState

}