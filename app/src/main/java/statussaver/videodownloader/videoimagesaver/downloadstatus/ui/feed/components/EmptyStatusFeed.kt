package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.feed.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButton
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppImage
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.rememberAppIcon

@Composable
fun EmptyStatusFeed(
    @DrawableRes illustration: Int,
    text: String,
    actionButtonText: String,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    subText: String = stringResource(R.string.it_will_appear_here),
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        AppImage(
            image = rememberAppIcon(illustration),
            modifier = Modifier
                .widthIn(max = 262.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillWidth,
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = text,
            style = typography.bodyMedium
        )
        Text(
            text = subText,
            style = typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        AppButton(
            modifier = Modifier
                .widthIn(max = 380.dp)
                .fillMaxWidth(),
            text = actionButtonText,
            onClick = onActionClick,
        )
    }
}