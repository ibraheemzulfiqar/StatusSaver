package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.BottomSheetDismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.NumberedText
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButton
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppImage
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.rememberAppIcon

@Composable
fun DialogHowToUse(state: BottomSheetDismissibleState) {
    AppBottomSheet(
        state = state,
        eventName = "HowToUse"
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(vertical = 32.dp, horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically),
        ) {
            Text(
                text = stringResource(R.string.how_to_use),
                style = typography.headlineLarge,
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            ) {
                NumberedText(
                    number = 1,
                    text = stringResource(R.string.open_wa_view_a_status),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )
                AppImage(
                    image = rememberAppIcon(R.drawable.ils_how_it_works_1),
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .aspectRatio(5f / 4f),
                )
                NumberedText(
                    number = 2,
                    text = stringResource(R.string.download_the_status_you_like),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                )
                AppImage(
                    image = rememberAppIcon(R.drawable.ils_how_it_works_2),
                    modifier = Modifier
                        .fillMaxWidth(0.75f)
                        .aspectRatio(5f / 4f),
                )
            }

            AppButton(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                text = stringResource(R.string.okay),
                onClick = state::dismiss,
            )
        }
    }
}
