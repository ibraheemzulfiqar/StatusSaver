package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.viewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.StatusSaverApp
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.toast
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.SAVED
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusRepository
import javax.inject.Inject

@HiltViewModel
class StatusViewerViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val repository: StatusRepository,
) : AndroidViewModel(application) {

    private val route = savedStateHandle.toRoute<StatusViewerRoute>()

    val statusResult = if (route.provider == SAVED) {
        repository.savedStatuses
    } else {
        repository.whatsappStatuses
    }

    fun delete(status: Status) {
        viewModelScope.launch {
            val deleted = repository.deleteStatus(status)

            val context = getApplication<StatusSaverApp>()

            if (deleted) {
                context.toast(R.string.deleted_successfully)
            } else {
                context.toast(R.string.something_went_wrong)
            }
        }
    }

    fun save(status: Status) {
        viewModelScope.launch {
            val saved = repository.saveStatus(status)

            val context = getApplication<StatusSaverApp>()

            if (saved) {
                context.toast(R.string.saved_successfully)
            } else {
                context.toast(R.string.something_went_wrong)
            }
        }
    }

}