package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.viewer

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackScreenViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider

@Serializable
data class StatusViewerRoute(
    val path: String,
    val provider: StatusProvider,
)

fun NavHostController.navigateToStatusViewer(status: Status) {
    navigate(StatusViewerRoute(status.path, status.provider))
}

fun NavGraphBuilder.statusViewerRoute(
    onNavigateUp: () -> Unit,
    onNavigateToEnhancer: (Status) -> Unit,
) {
    composable<StatusViewerRoute> {
        val route = it.toRoute<StatusViewerRoute>()

        StatusViewerScreen(
            initialPath = route.path,
            onNavigateToEnhancer = onNavigateToEnhancer,
            onNavigate = onNavigateUp,
        )

        TrackScreenViewEvent(
            name = "StatusViewer",
            "provider" to route.provider.name,
        )
    }
}

@Composable
fun StatusViewerScreen(
    initialPath: String,
    viewModel: StatusViewerViewModel = hiltViewModel(),
    onNavigateToEnhancer: (Status) -> Unit,
    onNavigate: () -> Unit,
) {

    val statusResult by viewModel.statusResult.collectAsState()

    StatusViewerScreenContent(
        initialPath = initialPath,
        statusResult = statusResult,
        onNavigateToEnhancer = onNavigateToEnhancer,
        onNavigate = onNavigate,
        onSave = viewModel::save,
        onDelete = viewModel::delete,
    )
}