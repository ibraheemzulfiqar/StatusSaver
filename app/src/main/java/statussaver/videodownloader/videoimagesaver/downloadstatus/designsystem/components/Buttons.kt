package statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components

import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp


@Composable
fun AppOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
) {
    OutlinedButton(
        modifier = modifier.defaultMinSize(
            minHeight = AppButtonDefaults.MinHeight
        ),
        onClick = onClick,
        enabled = enabled,
        colors = colors,
    ) {
        Text(text)
    }
}

@Composable
fun AppButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
) {
    Button(
        modifier = modifier.defaultMinSize(
            minHeight = AppButtonDefaults.MinHeight
        ),
        onClick = onClick,
        enabled = enabled,
        colors = colors,
    ) {
        Text(text)
    }
}

@Composable
fun AppIconButton(
    icon: AppIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = AppButtonDefaults.IconButtonSize,
) {
    IconButton(
        modifier = modifier.requiredSize(size),
        onClick = onClick,
    ) {
        AppIcon(icon)
    }
}

@Composable
fun AppImageButton(
    image: AppIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    buttonSize: Dp = AppButtonDefaults.IconButtonSize,
) {
    IconButton(
        modifier = modifier.requiredSize(buttonSize),
        onClick = onClick,
    ) {
        AppImage(image)
    }
}


object AppButtonDefaults {
    val MinHeight = 54.dp
    val IconButtonSize = 44.dp

    @Composable
    fun dimColors() = ButtonDefaults.buttonColors(
        containerColor = colorScheme.onSurfaceVariant,
        contentColor = colorScheme.onSurface,
        disabledContentColor = colorScheme.onSurface.copy(0.5f),
        disabledContainerColor = colorScheme.onSurfaceVariant.copy(0.5f),
    )

}
