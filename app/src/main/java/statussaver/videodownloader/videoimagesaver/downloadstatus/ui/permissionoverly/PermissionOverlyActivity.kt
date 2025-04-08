package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.permissionoverly

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.runNonFatal
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.StatusSaverTheme

@AndroidEntryPoint
class PermissionOverlyActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StatusSaverTheme {
                PermissionOverlyContent()
            }
        }
    }

    companion object {

        fun start(context: Context) {
            runNonFatal("start_permission_overly") {
                val intent = Intent(context, PermissionOverlyActivity::class.java)
                context.startActivity(intent)
            }
        }
    }
}
