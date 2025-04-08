package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.LocalAnalyticsHelper
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.TrackDialogViewEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.logEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.DismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dismissed
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButton
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButtonDefaults
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppImage
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.VerticalSpacer
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons

@Composable
fun DialogEnhanceResult(
    state: DismissibleState,
    path: String?,
    onViewImageClick: (String) -> Unit,
    onFeedbackClick: () -> Unit,
) {
    if (state.dismissed) return

    val analytics = LocalAnalyticsHelper.current

    ModalBottomSheet(
        onDismissRequest = state::dismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = colorScheme.surfaceContainer,
        contentColor = colorScheme.onSurface,
        dragHandle = {},
    ) {
        if (path != null) {
            EnhancerResultDialogContent(
                icon = AppIcons.Success,
                title = stringResource(R.string.finished_successfully),
                body = stringResource(R.string.this_is_an_experimental_feature_your_feedback_is_valuable_in_helping_us_improve),
                actionText = stringResource(R.string.view_image),
                onActionClick = {
                    state.dismiss()
                    onViewImageClick(path)

                    analytics.logEvent(
                        name = "enhancer_result_success_dialog",
                        "action" to "ViewImage",
                    )
                },
                onDismissClick = {
                    state.dismiss()

                    analytics.logEvent(
                        name = "enhancer_result_success_dialog",
                        "action" to "Dismiss",
                    )
                }
            )
        } else {
            EnhancerResultDialogContent(
                icon = AppIcons.Error,
                title = stringResource(R.string.something_went_wrong),
                body = stringResource(R.string.oops_an_error_occurred_this_is_an_experimental_feature_your_feedback_will_help_us_improve),
                actionText = stringResource(R.string.feedback),
                onActionClick = {
                    state.dismiss()
                    onFeedbackClick()

                    analytics.logEvent(
                        name = "enhancer_result_error_dialog",
                        "action" to "Feedback",
                    )
                },
                onDismissClick = {
                    state.dismiss()

                    analytics.logEvent(
                        name = "enhancer_result_error_dialog",
                        "action" to "Dismiss",
                    )
                }
            )
        }
    }

    TrackDialogViewEvent(
        name = "EnhanceResult",
        "success" to (path != null),
    )
}

@Composable
private fun EnhancerResultDialogContent(
    icon: AppIcon,
    title: String,
    body: String,
    actionText: String,
    onActionClick: () -> Unit,
    onDismissClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        AppImage(icon)

        VerticalSpacer(16.dp)

        Text(
            text = title,
            style = typography.headlineLarge,
        )

        VerticalSpacer(16.dp)

        Text(
            text = body,
            style = typography.bodyMedium,
            color = colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        VerticalSpacer(24.dp)

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = actionText,
            onClick = onActionClick,
        )

        VerticalSpacer(8.dp)

        AppButton(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.dismiss),
            onClick = onDismissClick,
            colors = AppButtonDefaults.dimColors(),
        )
    }
}