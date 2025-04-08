package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.language

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.shady.language.LanguageManager
import kotlinx.serialization.Serializable
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackScreenViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.MainActivity

@Serializable
object LanguageRoute

fun NavHostController.navigateToLanguage() {
    navigate(LanguageRoute)
}

fun NavGraphBuilder.languageRoute(
    onNavigateForward: () -> Unit,
) {
   composable<LanguageRoute> {
       LanguageScreen(onNavigateForward = onNavigateForward)

       TrackScreenViewEvent("Language")
   }
}

@Composable
fun LanguageScreen(
    viewModel: LanguageViewModel = hiltViewModel(),
    onNavigateForward: () -> Unit,
) {
    val languageManager = LanguageManager.getInstance()
    val context = LocalContext.current

    LanguageScreenContent(
        onNavigateForward = { language ->
            onNavigateForward()

            language?.let {
                languageManager.setLanguage(context, it)
                MainActivity.restart(context)
            }

            viewModel.saveLanguageBoardingDone()
        },
    )
}