package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.home

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.StatusSaverApp
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.toast
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusRepository
import statussaver.videodownloader.videoimagesaver.downloadstatus.datastore.UserPreference
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val repository: StatusRepository,
    private val preference: UserPreference,
) : AndroidViewModel(application) {

    private val statusBeingSaved = mutableStateListOf<Status>()

    val whatsappStatues = repository.whatsappStatuses
    val savedStatuses = repository.savedStatuses

    val notificationDeniedPermanently = preference.notificationDeniedPermanently
    val storageDeniedPermanently = preference.storageDeniedPermanently

    fun saveStatus(status: Status) {
        statusBeingSaved.add(status)

        viewModelScope.launch {
            val saved = repository.saveStatus(status)

            val context = getApplication<StatusSaverApp>()

            if (saved) {
                context.toast(R.string.saved_successfully)
            } else {
                context.toast(R.string.something_went_wrong)
            }

            statusBeingSaved.remove(status)
        }
    }

    fun saveNotificationDeniedPermanently() {
        preference.setNotificationDeniedPermanently(true)
    }

    fun saveStorageDeniedPermanently() {
        preference.setStorageDeniedPermanently(true)
    }

    fun enableNotification(enable: Boolean) {
        preference.setNotificationEnable(enable)
    }

    fun isSaving(status: Status): Boolean {
        return statusBeingSaved.contains(status)
    }
}