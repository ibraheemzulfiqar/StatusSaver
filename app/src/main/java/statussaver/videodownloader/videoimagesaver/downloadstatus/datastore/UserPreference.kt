package statussaver.videodownloader.videoimagesaver.downloadstatus.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.AnalyticsHelper
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.logEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreference @Inject constructor(
    @ApplicationContext private val context: Context,
    private val analytics: AnalyticsHelper,
) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val data = context.dataStore.data

    private val externalScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    val notificationEnable = getState(KEY_NOTIFICATION_ENABLE, false)

    val autoSaveEnable = getState(KEY_AUTO_SAVE_ENABLE, false)

    val notificationDeniedPermanently = getState(KEY_NOTIFICATION_DENIED_PERMANENTLY, false)

    val storageDeniedPermanently = getState(KEY_STORAGE_DENIED_PERMANENTLY, false)

    val selectedLanguage = data.map { it[KEY_SELECTED_LANGUAGE] }

    val languageBoardingDone = data.map { it[KEY_LANGUAGE_BOARDING] ?: false }

    fun setNotificationEnable(value: Boolean) = set(KEY_NOTIFICATION_ENABLE, value)

    fun setAutoSaveEnable(value: Boolean) = set(KEY_AUTO_SAVE_ENABLE, value)

    fun setNotificationDeniedPermanently(value: Boolean) = set(KEY_NOTIFICATION_DENIED_PERMANENTLY, value)

    fun setStorageDeniedPermanently(value: Boolean) = set(KEY_STORAGE_DENIED_PERMANENTLY, value)

    fun setLanguageBoardingDone(value: Boolean) = set(KEY_LANGUAGE_BOARDING, value)

    fun setLanguage(value: String?) = set(KEY_SELECTED_LANGUAGE, value)

    //region UTILS

    private fun <T> getState(key: Preferences.Key<T>, default: T) =
        data.map { it[key] ?: default }.stateIn(externalScope, Eagerly, default)

    private fun <T> set(key: Preferences.Key<T>, value: T?) = externalScope.launch {
        context.dataStore.edit {
            if (value == null) {
                it.remove(key)
            } else {
                it[key] = value
            }
        }

        analytics.logEvent(
            name = "user_preference_change",
            key.name to value
        )
    }

    //endregion

    companion object {
        val KEY_NOTIFICATION_ENABLE = booleanPreferencesKey("alert_service")
        val KEY_AUTO_SAVE_ENABLE = booleanPreferencesKey("auto_save_key")
        val KEY_TERMS_ACCEPTED = booleanPreferencesKey("terms_accepted")
        val KEY_SELECTED_LANGUAGE = stringPreferencesKey("selected_language")
        val KEY_LANGUAGE_BOARDING = booleanPreferencesKey("language_boarding")
        val KEY_WAS_APP_RATED = booleanPreferencesKey("was_rated")
        val KEY_SAVE_STATUS_COUNT = intPreferencesKey("save_status_count")
        val KEY_NOTIFICATION_DENIED_PERMANENTLY = booleanPreferencesKey("notification_denied")
        val KEY_STORAGE_DENIED_PERMANENTLY = booleanPreferencesKey("storage_denied")
    }

}