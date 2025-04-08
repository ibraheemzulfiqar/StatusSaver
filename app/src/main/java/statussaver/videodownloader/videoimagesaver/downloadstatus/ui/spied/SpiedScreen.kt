package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.spied

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackScreenViewEvent

@Serializable
object SpiedRoute

fun NavHostController.navigateToSpied() {
    navigate(SpiedRoute)
}

fun NavGraphBuilder.spiedRoute(
    onNavigateUp: () -> Unit,
) {
    composable<SpiedRoute> {
        SpiedScreenContent(
            onNavigate = onNavigateUp,
        )

        TrackScreenViewEvent("BeingSpiedOn")
    }
}