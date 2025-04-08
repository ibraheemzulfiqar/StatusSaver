package statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp

@Composable
fun AppTopBar(
    modifier: Modifier = Modifier,
    title: String = "",
    navigationIcon: AppIcon? = null,
    onNavigationClick: (() -> Unit)? = null,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        title = {
            Text(
                text = title,
                style = typography.titleLarge,
            )
        },
        navigationIcon = {
            if (navigationIcon != null && onNavigationClick != null) {
                AppIconButton(
                    icon = navigationIcon,
                    onClick = onNavigationClick,
                    size = Dp.Unspecified,
                )
            }
        },
        actions = actions,
        colors = colors,
    )
}