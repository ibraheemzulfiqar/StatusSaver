package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import statussaver.videodownloader.videoimagesaver.downloadstatus.R.string
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.SpanStyleUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.DimenTokens

@Composable
fun UseThisFolderGuideText(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        NumberedText(
            number = 1,
            text = stringResource(string.Permission_click_allow),
            emphasizeText = stringResource(string.Permission_allow_emphasized),
        )
        NumberSpacer()
        NumberedText(
            number = 2,
            text = stringResource(string.Permission_find_and_click_use_this_folder),
            emphasizeText = stringResource(string.Permission_use_this_folder_emphasized),
        )
    }
}

@Composable
fun StoragePermissionGuideText() {
    NumberedText(
        number = 1,
        text = stringResource(string.Permission_click_allow),
        emphasizeText = stringResource(string.Permission_allow_emphasized),
    )
    NumberSpacer()
    NumberedText(
        number = 2,
        text = stringResource(string.Permission_allow_access_to_your_device_storage),
    )
}

@Composable
fun StoragePermissionSettingsGuideText() {
    NumberedText(
        number = 1,
        text = stringResource(string.Permission_click_settings),
        emphasizeText = stringResource(string.Permission_settings_emphasized),
    )
    NumberSpacer()
    NumberedText(
        number = 2,
        text = stringResource(string.Permission_go_to_permissions_enable_storage),
    )
}

@Composable
fun NumberedText(
    number: Int,
    text: String,
    modifier: Modifier = Modifier,
    emphasizeText: String? = null,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(12.dp),
    verticalAlignment: Alignment.Vertical = Alignment.Top,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
    ) {
        NumberCircle(number)

        if (emphasizeText != null) {
            Text(
                SpanStyleUtils.emphasizeString(
                    text = text,
                    emphasize = emphasizeText,
                ),
                style = typography.bodyMedium,
                lineHeight = 20.sp,
            )
        } else {
            Text(
                text = text,
                style = typography.bodyMedium,
            )
        }

    }
}

@Composable
fun NumberCircle(
    number: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(DimenTokens.NumberCircleSize)
            .clip(CircleShape)
            .background(colorScheme.primary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "$number",
            fontSize = 12.sp,
            style = typography.bodyMedium,
            textAlign = TextAlign.Center,
            lineHeight = 12.sp,
        )
    }
}

@Composable
fun NumberSpacer(
    height: Dp = 16.dp,
) {
    Spacer(Modifier.height(height))
}