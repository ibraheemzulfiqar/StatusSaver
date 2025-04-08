package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.permissionoverly

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.findActivity
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppImage
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.VerticalSpacer
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.StatusSaverTheme

@Composable
fun PermissionOverlyContent() {
    val activity = LocalContext.current.findActivity()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable(
                interactionSource = null,
                indication = null,
                onClick = { activity?.finish() },
            ),
        contentAlignment = Alignment.BottomCenter,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black, Color.Black)
                    )
                )
                .padding(top = 48.dp)
                .safeDrawingPadding()
                .padding(horizontal = 16.dp)
        ) {

            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.allow_access_to_use_this_folder_to_save_status),
                fontSize = 16.sp,
                color = Color.White,
                textAlign = TextAlign.Center,
            )

            VerticalSpacer(12.dp)

            Box(
                contentAlignment = Alignment.BottomCenter,
            ) {
                UseThisFolderButton(onClick = { activity?.finish() })

                AppImage(
                    image = AppIcons.HandClick,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .padding(end = 16.dp)
                        .offset(y = 2.dp)
                )
            }

            VerticalSpacer(8.dp)
        }
    }
}

@Composable
fun UseThisFolderButton(
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(100))
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
            .background(Color(0xFF60C2FF))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = stringResource(R.string.use_this_folder),
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PermissionOverlyContentPrev() {
    StatusSaverTheme {
        PermissionOverlyContent()
    }
}