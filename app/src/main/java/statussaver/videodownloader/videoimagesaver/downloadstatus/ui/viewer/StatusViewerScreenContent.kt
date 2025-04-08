package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.viewer

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.GlideImage
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.signature.ObjectKey
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.toast
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.LoadingScreen
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.VideoPlayer
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogDeleteStatus
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberDismissible
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.zoomable.rememberZoomState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.zoomable.zoomable
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.IntentUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.runNonFatal
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusProvider.SAVED
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusResult
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButtonDefaults
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIconButton
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppTopBar
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.DimenTokens
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppImage
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.White


@Composable
fun StatusViewerScreenContent(
    initialPath: String,
    statusResult: StatusResult,
    onNavigateToEnhancer: (Status) -> Unit,
    onDelete: (Status) -> Unit,
    onSave: (Status) -> Unit,
    onNavigate: () -> Unit,
) {
    if (statusResult is StatusResult.Success) {
        LaunchedEffect(statusResult) {
            if (statusResult.statuses.isEmpty()) {
                onNavigate()
            }
        }

        StatusViewerScreenContent(
            initialPath = initialPath,
            statuses = statusResult.statuses,
            onNavigateToEnhancer = onNavigateToEnhancer,
            onNavigate = onNavigate,
            onSave = onSave,
            onDelete = onDelete,
        )
    } else {
        LoadingScreen()
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StatusViewerScreenContent(
    initialPath: String,
    statuses: List<Status>,
    onNavigateToEnhancer: (Status) -> Unit,
    onDelete: (Status) -> Unit,
    onSave: (Status) -> Unit,
    onNavigate: () -> Unit,
) {
    val containerColor = Color.Black
    val contentColor = Color.White

    val initialIndex = remember(initialPath) {
        statuses
            .indexOfFirst { it.path == initialPath }
            .coerceAtLeast(0)
    }

    val pagerState = rememberPagerState(initialIndex) { statuses.size }
    val currentStatus: () -> Status? = {
        statuses.getOrNull(pagerState.settledPage)
    }

    val deleteDialog = rememberDismissible()

    DialogDeleteStatus(
        state = deleteDialog,
        onDelete = {
            currentStatus()?.let { onDelete(it) }
        }
    )

    Scaffold(
        topBar = {
            ViewerTopBar(
                currentStatus = currentStatus,
                onNavigateToEnhancer = onNavigateToEnhancer,
                onNavigate = onNavigate,
                containerColor = containerColor,
                contentColor = contentColor,
            )
        },
        containerColor = containerColor,
        contentColor = contentColor,
    ) { padding ->
        ViewerPager(
            pagerState = pagerState,
            statuses = statuses,
            containerColor = containerColor,
            onSave = onSave,
            onDelete = { deleteDialog.show() },
        )
    }
}

@Composable
fun ViewerTopBar(
    currentStatus: () -> Status?,
    onNavigateToEnhancer: (Status) -> Unit,
    onNavigate: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Black,
    contentColor: Color = White,
) {
    val context = LocalContext.current

    AppTopBar(
        navigationIcon = AppIcons.Navigation,
        onNavigationClick = onNavigate,
        modifier = modifier.background(
            brush = Brush.verticalGradient(
                colors = listOf(containerColor, Color.Transparent)
            )
        ),
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
            navigationIconContentColor = contentColor,
            actionIconContentColor = contentColor,
        )
    ) {
        val status = currentStatus()

        AnimatedVisibility(
            visible = status?.isImage == true && !status.displayName.startsWith("Enhanced"),
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut()
        ) {
            ImageEnhancerButton(
                onClick = {
                    currentStatus()
                        ?.takeIf { it.isImage }
                        ?.let { onNavigateToEnhancer(it) }
                }
            )
        }

        AppIconButton(
            icon = AppIcons.Repost,
            onClick = {
                runNonFatal("repost_status") {
                    IntentUtils.repostStatus(
                        context = context,
                        status = currentStatus()!!,
                    )
                }.onFailure {
                    context.toast(R.string.something_went_wrong)
                }
            },
        )
    }
}

@Composable
fun ViewerPager(
    statuses: List<Status>,
    pagerState: PagerState,
    onDelete: (Status) -> Unit,
    onSave: (Status) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.Black,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(containerColor),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = modifier.fillMaxSize(),
            key = { statuses[it].path },
            beyondViewportPageCount = 1,
        ) { index ->
            val status = statuses[index]

            if (status.isImage) {
                ViewerImage(status)
            } else {
                VideoPlayer(
                    status = status,
                    pageVisible = pagerState.settledPage == index,
                )
            }
        }

        statuses.getOrNull(pagerState.settledPage)?.let { status ->
            ViewerButtonsRow(
                status = status,
                onSave = onSave,
                onDelete = onDelete,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color.Transparent, containerColor)
                        )
                    )
            )
        }
    }
}

@Composable
fun ViewerImage(
    status: Status,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        GlideImage(
            modifier = modifier
                .fillMaxSize()
                .zoomable(
                    zoomState = rememberZoomState(),
                    zoomEnabled = status.isImage,
                ),
            model = status.path,
            contentDescription = null,
        ) { builder ->
            builder
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .signature(ObjectKey("${status.path}-${status.dateModified}"))
        }
    }
}

@Composable
fun ViewerButtonsRow(
    status: Status,
    onDelete: (Status) -> Unit,
    onSave: (Status) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 16.dp)
            .height(DimenTokens.ViewerButtonContainerHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ViewerButton(
            modifier = Modifier.weight(1f),
            text = stringResource(R.string.share),
            icon = AppIcons.Share,
            onClick = {
                runNonFatal("share_status") {
                    IntentUtils.shareStatus(
                        context = context,
                        status = status,
                    )
                }.onFailure {
                    context.toast(R.string.something_went_wrong)
                }
            },
        )

        if (status.provider == SAVED) {
            ViewerButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.delete),
                icon = AppIcons.Delete,
                onClick = { onDelete(status) },
            )
        } else if (status.isSaved) {
            ViewerButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.saved),
                icon = AppIcons.Downloaded,
                onClick = {
                    context.toast(R.string.already_saved)
                },
            )
        } else {
            ViewerButton(
                modifier = Modifier.weight(1f),
                text = stringResource(R.string.save),
                icon = AppIcons.Download,
                onClick = { onSave(status) },
            )
        }
    }
}


@Composable
fun ViewerButton(
    text: String,
    icon: AppIcon,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        modifier = modifier.defaultMinSize(
            minHeight = AppButtonDefaults.MinHeight
        ),
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = White.copy(alpha = 0.45f),
            contentColor = White,
        ),
    ) {
        AppIcon(icon)
        Spacer(Modifier.width(8.dp))
        Text(
            text = text,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun ImageEnhancerButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colorSets = listOf(
        listOf(Color(0xFF651B87), Color(0xFF7F1657)),
        listOf(Color(0xFF034974), Color(0xFF07429B)),
        listOf(Color(0xFF0E4F1A), Color(0xFF005136)),
        listOf(Color(0xFF81183E), Color(0xFF812105))
    )

    val transition = rememberInfiniteTransition(label = "gradientAnimation")

    val progress by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "gradientProgress"
    )

    // Calculate the current color set based on progress
    val currentColors = remember(progress) {
        val index = (progress * (colorSets.size - 1)).toInt()
        val nextIndex = (index + 1) % colorSets.size
        val progress = progress * (colorSets.size - 1) - index
        colorSets[index].zip(colorSets[nextIndex]) { start, end ->
            lerp(start, end, progress)
        }
    }

    val gradientBrush = Brush.linearGradient(
        colors = currentColors,
        start = Offset(0f, Float.POSITIVE_INFINITY),
        end = Offset(Float.POSITIVE_INFINITY, 0f)
    )

    IconButton(
        modifier = modifier.requiredSize(AppButtonDefaults.IconButtonSize),
        onClick = onClick,
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .size(36.dp)
                .background(brush = gradientBrush),
            contentAlignment = Alignment.Center,
        ) {
            AppImage(AppIcons.Enhance)
        }
    }
}