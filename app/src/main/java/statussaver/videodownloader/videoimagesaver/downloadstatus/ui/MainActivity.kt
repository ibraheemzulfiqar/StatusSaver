package statussaver.videodownloader.videoimagesaver.downloadstatus.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.toArgb
import com.shady.language.LanguageManager
import dagger.Lazy
import dagger.hilt.android.AndroidEntryPoint
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.AnalyticsHelper
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.LocalAnalyticsHelper
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.isAndroidMin11
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusRepository
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.Surface
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var statusRepository: Lazy<StatusRepository>

    @Inject
    lateinit var analyticsHelper: Lazy<AnalyticsHelper>

    private val wasRestarted: Boolean by lazy {
        intent.getBooleanExtra(EXTRA_RESTART, false)
    }

    @SuppressLint("MissingSuperCall")
    override fun onConfigurationChanged(newConfig: Configuration) {
        LanguageManager.getInstance().applyLocale(this)
        super.onConfigurationChanged(newConfig)
    }

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(
            newBase?.let { LanguageManager.getInstance().applyLocale(it) }
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (isAndroidMin11()) {
            enableEdgeToEdge()
        } else {
            enableEdgeToEdge(
                statusBarStyle = SystemBarStyle.dark(Surface.toArgb()),
                navigationBarStyle = SystemBarStyle.dark(Surface.toArgb()),
            )
        }

        setContent {
            CompositionLocalProvider(
                LocalAnalyticsHelper provides analyticsHelper.get(),
            ) {
                AppNavHost(
                    wasRestarted = wasRestarted,
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        statusRepository.get().refreshStatuses()
    }

    companion object {
        const val EXTRA_OPEN_SETTINGS = "_EXTRA_OPEN_SETTINGS_"
        const val EXTRA_RESTART = "_EXTRA_RESTART_"


        fun restart(context: Context) {
            val intent = createIntent(context, restart = true)
            context.startActivity(intent)
        }

        fun createIntent(
            context: Context,
            openSettings: Boolean = false,
            restart: Boolean = false
        ) = Intent(context, MainActivity::class.java).apply {
            putExtra(EXTRA_OPEN_SETTINGS, openSettings)
            putExtra(EXTRA_RESTART, restart)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
    }

}