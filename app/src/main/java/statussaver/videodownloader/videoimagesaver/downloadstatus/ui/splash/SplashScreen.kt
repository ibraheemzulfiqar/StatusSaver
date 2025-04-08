package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import kotlinx.serialization.Serializable
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackScreenViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.splash.SplashState.Success

@Serializable
data object SplashRoute

fun NavGraphBuilder.splashRoute(
    onNavigateToLanguage: () -> Unit,
    onNavigateToHome: () -> Unit,
) {
    composable<SplashRoute> {
        SplashScreen(
            onNavigateToLanguage = onNavigateToLanguage,
            onNavigateToHome = onNavigateToHome,
        )

        TrackScreenViewEvent("Splash")
    }
}

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToLanguage: () -> Unit,
    onNavigateToHome: () -> Unit,
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(state) {
        val cacheState = state

        if (cacheState is Success) {
            if (cacheState.languageBoardingDone) {
                onNavigateToHome()
            } else {
                onNavigateToLanguage()
            }
        }
    }

    SplashScreenContent()

}