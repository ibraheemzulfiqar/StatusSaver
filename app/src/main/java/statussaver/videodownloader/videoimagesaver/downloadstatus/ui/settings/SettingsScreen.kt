package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.shady.language.LanguageManager
import kotlinx.serialization.Serializable
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackScreenViewEvent

@Serializable
data object SettingsRoute

fun NavHostController.navigateToSettings() {
    navigate(SettingsRoute)
}

fun NavGraphBuilder.settingsRoute(
    onNavigateUp: () -> Unit
) {
    composable<SettingsRoute> {
        SettingsScreen(onNavigateUp = onNavigateUp)

        TrackScreenViewEvent("Settings")
    }
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = hiltViewModel(),
    onNavigateUp: () -> Unit,
) {
    val languageManager = LanguageManager.getInstance()

    val notificationEnable by viewModel.notificationEnable.collectAsState()
    val autoSaveEnable by viewModel.autoSaveEnable.collectAsState()
    val notificationDeniedPermanently by viewModel.notificationDeniedPermanently.collectAsState()
    val language by languageManager.language.collectAsState(null)

    SettingsScreenContent(
        language = language,
        notificationEnable = notificationEnable,
        autoSaveEnable = autoSaveEnable,
        notificationDeniedPermanently = notificationDeniedPermanently,
        onToggleNotification = { viewModel.toggleNotification() },
        onToggleAutoSave = { viewModel.toggleAutoSave() },
        onNotificationDeniedPermanently = { viewModel.setNotificationDeniedPermanently() },
        onNavigateUp = onNavigateUp,
    )
}