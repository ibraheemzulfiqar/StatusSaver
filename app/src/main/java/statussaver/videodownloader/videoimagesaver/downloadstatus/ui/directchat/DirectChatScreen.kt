package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.directchat

import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackScreenViewEvent

@Serializable
data object DirectChatRoute

fun NavHostController.navigateToDirectChat() {
    navigate(DirectChatRoute)
}

fun NavGraphBuilder.directChatRoute(onNavigateUp: () -> Unit) {
    composable<DirectChatRoute> {
        DirectChatScreen(
            onNavigateUp = onNavigateUp,
        )

        TrackScreenViewEvent("DirectChat")
    }
}

@Composable
fun DirectChatScreen(
    onNavigateUp: () -> Unit,
) {
    DirectChatScreenContent(
        onNavigateUp = onNavigateUp,
    )
}