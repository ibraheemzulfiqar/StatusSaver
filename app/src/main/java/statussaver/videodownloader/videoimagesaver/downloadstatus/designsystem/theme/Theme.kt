package statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = White,
    surface = Surface,
    surfaceContainer = SurfaceContainer,
    surfaceContainerLow = SurfaceContainerLight,
    onSurface = White,
    onSurfaceVariant = Mute,
    outline = Mute,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    background = Surface,
    onBackground = White,
    error = Red,
    onError = White,
    errorContainer = Red,
    onErrorContainer = White,
)

@Composable
fun StatusSaverTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}