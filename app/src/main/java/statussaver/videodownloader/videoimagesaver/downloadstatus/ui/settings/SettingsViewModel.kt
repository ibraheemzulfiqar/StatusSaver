package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import statussaver.videodownloader.videoimagesaver.downloadstatus.StatusSaverApp
import statussaver.videodownloader.videoimagesaver.downloadstatus.datastore.UserPreference
import statussaver.videodownloader.videoimagesaver.downloadstatus.service.NotifyStatusService
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preference: UserPreference,
    application: Application,
) : AndroidViewModel(application) {

    val notificationEnable = preference.notificationEnable

    val autoSaveEnable = preference.autoSaveEnable

    val notificationDeniedPermanently = preference.notificationDeniedPermanently

    fun toggleNotification() {
        val value = !notificationEnable.value

        preference.setNotificationEnable(value)
    }

    fun toggleAutoSave() {
        val value = !autoSaveEnable.value

        preference.setAutoSaveEnable(value)
    }

    fun setNotificationDeniedPermanently() {
        preference.setNotificationDeniedPermanently(true)
    }

    private fun startService() {
        val context = getApplication<StatusSaverApp>()

        NotifyStatusService.stop(context)
        NotifyStatusService.start(context)
    }

    private fun stopService() {
        val context = getApplication<StatusSaverApp>()
        NotifyStatusService.stop(context)
    }

}