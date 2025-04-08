package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackScreenViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status

@Serializable
data class ImageEnhancerRoute(
    val path: String,
    val displayName: String,
    val dateModified: Long,
)

fun NavHostController.navigateToImageEnhancer(status: Status) {
    navigate(ImageEnhancerRoute(status.path, status.displayName, status.dateModified))
}

fun NavGraphBuilder.imageEnhancerRoute(
    onNavigateToViewer: (String) -> Unit,
    onNavigateUp: () -> Unit,
) {
    composable<ImageEnhancerRoute> {
        val route = it.toRoute<ImageEnhancerRoute>()

        ImageEnhancerScreen(
            path = route.path,
            dateModified = route.dateModified,
            onNavigateToImage = onNavigateToViewer,
            onNavigate = onNavigateUp,
        )

        TrackScreenViewEvent("ImageEnhancer")
    }
}

@Composable
fun ImageEnhancerScreen(
    path: String,
    dateModified: Long,
    viewModel: ImageEnhancerViewModel = hiltViewModel(),
    onNavigateToImage: (String) -> Unit,
    onNavigate: () -> Unit,
) {
    ImageEnhancerScreenContent(
        state = viewModel.state,
        path = path,
        dateModified = dateModified,
        onEnhance = viewModel::enhanceImage,
        onNavigate = onNavigate,
        onNavigateToImage = onNavigateToImage,
    )
}