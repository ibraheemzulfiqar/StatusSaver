package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogEnhanceProgress
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogEnhanceResult
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberDismissible
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.zoomable.rememberZoomState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.zoomable.zoomable
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.IntentUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.SpanStyleUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.DimenTokens
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButtonDefaults
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppTopBar
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.VerticalSpacer
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.BetterBlack
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.RichYellow
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.StatusSaverTheme
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer.EnhancerState.Finish
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.enhancer.EnhancerState.Loading

@Preview
@Composable
private fun ImageEnhancerScreenContentPrev() {
    StatusSaverTheme {
        ImageEnhancerScreenContent(
            state = EnhancerState.Idle,
            path = "",
            dateModified = 0L,
            onEnhance = {},
            onNavigateToImage = {},
            onNavigate = {}
        )
    }
}

@Composable
fun ImageEnhancerScreenContent(
    state: EnhancerState,
    path: String,
    dateModified: Long,
    onEnhance: () -> Unit,
    onNavigateToImage: (String) -> Unit,
    onNavigate: () -> Unit,
) {
    val context = LocalContext.current

    val resultDialog = rememberDismissible(true) { visible ->
        if (visible.not()) onNavigate()
    }

    if (state is Loading) {
        DialogEnhanceProgress(progress = state.progress)
    }

    if (state is Finish) {
        DialogEnhanceResult(
            state = resultDialog,
            path = state.path,
            onViewImageClick = onNavigateToImage,
            onFeedbackClick = {
                IntentUtils.sendMail(context)
            },
        )
    }

    Scaffold(
        topBar = {
            AppTopBar(
                navigationIcon = AppIcons.Navigation,
                onNavigationClick = onNavigate,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ColumnRow(
                topContent = { alignVertical ->
                    if (alignVertical) {
                        Spacer(Modifier.weight(1f))
                    }

                    Text(
                        text = stringResource(R.string.enhance_image_quality),
                        fontWeight = FontWeight.Bold,
                        fontSize = 32.sp,
                        lineHeight = 40.sp,
                    )

                    VerticalSpacer(8.dp)

                    Text(
                        text = stringResource(R.string.experimental),
                        color = BetterBlack,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(100))
                            .background(RichYellow)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )

                    if (alignVertical) {
                        Spacer(Modifier.weight(1f))

                        EnhanceButton(onClick = onEnhance)

                        VerticalSpacer(16.dp)
                    } else {
                        VerticalSpacer(24.dp)
                    }
                },
                bottomContent = { alignVertical ->
                    EnhancerImage(
                        modifier = Modifier.weight(1f),
                        path = path,
                        dateModified = dateModified,
                    )

                    VerticalSpacer(24.dp)

                    DescriptionText()

                    if (!alignVertical) {
                        VerticalSpacer(16.dp)

                        EnhanceButton(onClick = onEnhance)

                        VerticalSpacer(16.dp)
                    }
                }
            )
        }
    }
}

@Composable
private fun EnhancerImage(
    path: String,
    dateModified: Long,
    modifier: Modifier = Modifier,
) {
    val inspectionMode = LocalInspectionMode.current
    val configuration = LocalConfiguration.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(BetterBlack),
        contentAlignment = Alignment.Center,
    ) {
        if (!inspectionMode) {
            key(configuration) {
                GlideImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .zoomable(rememberZoomState()),
                    model = path,
                    contentDescription = null,
                ) { builder ->
                    builder
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .signature(ObjectKey("${path}-${dateModified}"))
                }
            }
        }
    }
}

@Composable
private fun DescriptionText(
    modifier: Modifier = Modifier
) {
    val annotatedString = SpanStyleUtils.styledString(
        text = stringResource(R.string.of_photos_achieved_remarkable_results),
        styledText = stringResource(R.string._97_76),
        SpanStyle(fontWeight = FontWeight.Bold, color = RichYellow),
    )

    Text(
        modifier = modifier.fillMaxWidth(),
        text = annotatedString,
        style = typography.bodyMedium,
        color = colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun EnhanceButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val gradient = Brush.linearGradient(
        colors = listOf(Color(0xFF9747FF), Color(0xFF0066FF))
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AppButtonDefaults.MinHeight)
            .clip(RoundedCornerShape(100))
            .background(gradient)
            .clickable(
                onClick = onClick,
                role = Role.Button,
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIcon(AppIcons.Enhance)
        Text(
            text = stringResource(R.string.enhance),
            style = typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
        )
    }
}


@Composable
private fun ColumnRow(
    modifier: Modifier = Modifier,
    topContent: @Composable ColumnScope.(alignVertical: Boolean) -> Unit,
    bottomContent: @Composable ColumnScope.(alignVertical: Boolean) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val alignVertical = configuration.screenHeightDp < 500 && configuration.screenWidthDp > 500

    if (alignVertical) {
        Row(
            modifier = modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Column(
                modifier = Modifier.weight(1f),
                content = { topContent(true) }
            )
            Column(
                modifier = Modifier.weight(1f),
                content = { bottomContent(true) }
            )
        }

    } else {
        Column(
            modifier = modifier
                .widthIn(max = DimenTokens.MaxMaterialWidth)
                .fillMaxWidth()
        ) {
            topContent(false)
            bottomContent(false)
        }
    }
}
