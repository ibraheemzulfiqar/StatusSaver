package statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun AppIcon(
    icon: AppIcon,
    modifier: Modifier = Modifier,
    description: String? = null,
    tint: Color = LocalContentColor.current,
) {
    Icon(
        painter = icon.painter,
        contentDescription = description ?: icon.contentDescription,
        modifier = modifier,
        tint = tint,
    )
}

@Composable
fun AppImage(
    image: AppIcon,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    description: String? = null,
    tint: Color? = null,
) {
    Image(
        painter = image.painter,
        contentDescription = description ?: image.contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        colorFilter = tint?.let { ColorFilter.tint(it) },
    )
}

@Composable
fun rememberAppIcon(
    @DrawableRes id: Int,
    description: String? = null,
): AppIcon {
    return rememberAppIcon(
        painter = painterResource(id),
        description = description,
    )
}

@Composable
fun rememberAppIcon(
    painter: Painter,
    description: String? = null,
): AppIcon {
    return remember(painter, description) {
        AppIcon(painter, description)
    }
}

@Immutable
data class AppIcon(
    val painter: Painter,
    val contentDescription: String?,
)