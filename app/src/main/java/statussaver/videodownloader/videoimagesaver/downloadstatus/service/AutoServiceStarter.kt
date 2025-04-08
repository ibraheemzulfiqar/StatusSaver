package statussaver.videodownloader.videoimagesaver.downloadstatus.service

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import statussaver.videodownloader.videoimagesaver.downloadstatus.datastore.UserPreference
import javax.inject.Inject

class AutoServiceStarter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreference: UserPreference,
) {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun observeForever() {
        scope.launch {
            userPreference.notificationEnable.collect { enable ->
                if (enable) {
                    startService()
                } else {
                    if (!userPreference.autoSaveEnable.value) {
                        stopService()
                    }
                }
            }
        }

        scope.launch {
            userPreference.autoSaveEnable.collect { enable ->
                if (enable) {
                    startService()
                } else {
                    if (!userPreference.notificationEnable.value) {
                        stopService()
                    }
                }
            }
        }
    }

    private fun startService() {
        NotifyStatusService.start(context)
    }

    private fun stopService() {
        NotifyStatusService.stop(context)
    }

}