package statussaver.videodownloader.videoimagesaver.downloadstatus.ui

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.StatusSaverTheme
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.directchat.directChatRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.directchat.navigateToDirectChat
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer.imageEnhancerRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer.navigateToImageEnhancer
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.home.HomeRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.home.homeScreenRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.language.LanguageRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.language.languageRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.settings.navigateToSettings
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.settings.settingsRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.spied.navigateToSpied
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.spied.spiedRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.splash.SplashRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.splash.splashRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.viewer.StatusViewerRoute
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.viewer.navigateToStatusViewer
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.viewer.statusViewerRoute

@Composable
fun AppNavHost(
    wasRestarted: Boolean = false,
) {
    val navController = rememberNavController()

    val startDestination = if (wasRestarted) {
        HomeRoute
    } else {
        SplashRoute
    }

    StatusSaverTheme {
        NavHost(
            modifier = Modifier.background(colorScheme.surface),
            navController = navController,
            startDestination = startDestination,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {

            splashRoute(
                onNavigateToLanguage = {
                    navController.replace(LanguageRoute)
                },
                onNavigateToHome = {
                    navController.replace(HomeRoute)
                },
            )

            languageRoute(
                onNavigateForward = {
                    navController.replace(HomeRoute)
                }
            )

            homeScreenRoute(
                onNavigateToStatusViewer = navController::navigateToStatusViewer,
                onNavigateToDirectChat = navController::navigateToDirectChat,
                onNavigateToSpiedScreen = navController::navigateToSpied,
                onNavigateToSettings = navController::navigateToSettings,
            )

            settingsRoute(
                onNavigateUp = navController::navigateUp,
            )

            spiedRoute(onNavigateUp = navController::navigateUp)

            directChatRoute(
                onNavigateUp = navController::navigateUp,
            )

            statusViewerRoute(
                onNavigateUp = navController::navigateUp,
                onNavigateToEnhancer = navController::navigateToImageEnhancer,
            )

            imageEnhancerRoute(
                onNavigateUp = navController::navigateUp,
                onNavigateToViewer = { path ->
                    val route = StatusViewerRoute(path, StatusProvider.SAVED)

                    navController.replace(route)
                },
            )
        }
    }
}

fun <T : Any> NavHostController.replace(route: T) {
    navigate(route) {
        popUpTo(currentDestination?.route ?: "") {
            inclusive = true
        }
    }
}