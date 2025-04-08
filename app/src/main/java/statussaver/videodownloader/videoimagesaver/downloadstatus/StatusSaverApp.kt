package statussaver.videodownloader.videoimagesaver.downloadstatus

import android.app.Application
import android.os.Build
import com.shady.language.LanguageManager
import dagger.Lazy
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.isAndroidBelow13
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.isAndroidMin11
import statussaver.videodownloader.videoimagesaver.downloadstatus.datastore.LanguageDataStore
import statussaver.videodownloader.videoimagesaver.downloadstatus.datastore.UserPreference
import statussaver.videodownloader.videoimagesaver.downloadstatus.service.AutoServiceStarter
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class StatusSaverApp : Application() {

    @Inject
    lateinit var serviceStarter: Lazy<AutoServiceStarter>

    @Inject
    lateinit var preference: Lazy<UserPreference>

    @Inject
    lateinit var languageDataStore: Lazy<LanguageDataStore>

    override fun onCreate() {
        super.onCreate()

        initLogging()
        initLanguageManager()
        initServiceStarter()
        initNotification()
    }

    private fun initLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }

    private fun initServiceStarter() {
        serviceStarter.get().observeForever()
    }

    private fun initNotification() {
        if (isAndroidBelow13()) {
            preference.get().setNotificationEnable(true)
        }
    }

    private fun initLanguageManager() {
        LanguageManager.init(this, languageDataStore.get())
    }

}