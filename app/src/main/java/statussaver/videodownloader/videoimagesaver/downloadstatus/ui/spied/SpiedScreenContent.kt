package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.spied

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.NumberCircle
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.SpanStyleUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.DimenTokens
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppImage
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppTopBar
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.rememberAppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.StatusSaverTheme

@Preview
@Composable
private fun SpiedScreenContentPrev() {
    StatusSaverTheme { SpiedScreenContent({}) }
}

@Composable
fun SpiedScreenContent(
    onNavigate: () -> Unit,
) {

    val headingOne = SpanStyleUtils.styledString(
        text = stringResource(R.string.beind_spied_text_1),
        style = SpanStyle(fontWeight = FontWeight.Bold, color = colorScheme.onSurface),
        // styled Text
        stringResource(R.string.whatsapp_emphasize),
        stringResource(R.string.settings__emphasize),
        stringResource(R.string.linked_devices_emphasize),
    )

    val headingTwo = SpanStyleUtils.styledString(
        text = stringResource(R.string.beind_spied_text_2),
        style = SpanStyle(fontWeight = FontWeight.Bold, color = colorScheme.onSurface),
        // styled Text
        stringResource(R.string.device_status_emphasize),
        stringResource(R.string.linked_devices_emphasize)
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.being_spied_on),
                navigationIcon = AppIcons.Navigation,
                onNavigationClick = onNavigate,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = DimenTokens.MaxMaterialWidth)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                SpiedHeadingText(
                    number = 1,
                    text = headingOne,
                )

                AppImage(
                    image = rememberAppIcon(R.drawable.ils_being_spied_on_1),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .widthIn(max = 320.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.83f)
                )

                SpiedHeadingText(
                    number = 2,
                    text = headingTwo,
                )

                AppImage(
                    image = rememberAppIcon(R.drawable.ils_being_spied_on_2),
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .widthIn(max = 340.dp)
                        .fillMaxWidth()
                        .aspectRatio(1.96f)
                )
            }
        }
    }
}

@Composable
fun SpiedHeadingText(
    number: Int,
    text: AnnotatedString,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(contentAlignment = Alignment.Center) {
            NumberCircle(
                number = number,
            )

            Text(
                text = "A",
                style = typography.bodyLarge,
                lineHeight = 24.sp,
                letterSpacing = 0.2.sp,
                color = Color.Transparent,
            )
        }

        Text(
            text = text,
            style = typography.bodyLarge,
            lineHeight = 24.sp,
            letterSpacing = 0.2.sp,
        )
    }
}