package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackScreenViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status

@Serializable
data object HomeRoute

fun NavHostController.navigateToHome() {
    navigate(HomeRoute)
}

fun NavGraphBuilder.homeScreenRoute(
    onNavigateToStatusViewer: (Status) -> Unit,
    onNavigateToDirectChat: () -> Unit,
    onNavigateToSpiedScreen: () -> Unit,
    onNavigateToSettings: () -> Unit,
) {
    composable<HomeRoute> {
        HomeScreen(
            onStatusClick = onNavigateToStatusViewer,
            onDirectChatClick = onNavigateToDirectChat,
            onBeingSpiedClick = onNavigateToSpiedScreen,
            onSettingsClick = onNavigateToSettings,
        )

        TrackScreenViewEvent("Home")
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onStatusClick: (Status) -> Unit,
    onDirectChatClick: () -> Unit,
    onBeingSpiedClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val whatsappStatusResult by viewModel.whatsappStatues.collectAsState()
    val savedStatusResult by viewModel.savedStatuses.collectAsState()

    val notificationDeniedPermanently by viewModel.notificationDeniedPermanently.collectAsState()
    val storageDeniedPermanently by viewModel.storageDeniedPermanently.collectAsState()

    HomeScreenContent(
        whatsappStatusResult = whatsappStatusResult,
        savedStatusResult = savedStatusResult,
        notificationDeniedPermanently = notificationDeniedPermanently,
        storageDeniedPermanently = storageDeniedPermanently,
        isSaving = viewModel::isSaving,
        onStatusClick = onStatusClick,
        onDirectChatClick = onDirectChatClick,
        onSaveClick = viewModel::saveStatus,
        onBeingSpiedClick = onBeingSpiedClick,
        onSettingsClick = onSettingsClick,
        onEnableNotification = { viewModel.enableNotification(true) },
        onNotificationDeniedPermanently = viewModel::saveNotificationDeniedPermanently,
        onStorageDeniedPermanently = viewModel::saveStorageDeniedPermanently,
    )
}

