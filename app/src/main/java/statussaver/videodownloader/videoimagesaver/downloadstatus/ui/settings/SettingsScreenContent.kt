package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shady.language.Language
import com.statussaver.permission.isGranted
import com.statussaver.permission.rememberNotificationPermission
import statussaver.videodownloader.videoimagesaver.downloadstatus.BuildConfig
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.AppDetails
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.findActivity
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogLanguage
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogNotificationPermission
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberBottomSheetDismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberDismissible
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.IntentUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.launchInAppReview
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppTopBar
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.VerticalSpacer
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.settings.NotificationUsage.AutoSave
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.settings.NotificationUsage.Notification

enum class NotificationUsage { Notification, AutoSave }

@Composable
fun SettingsScreenContent(
    language: Language?,
    notificationEnable: Boolean,
    autoSaveEnable: Boolean,
    notificationDeniedPermanently: Boolean,
    onNotificationDeniedPermanently: () -> Unit,
    onToggleNotification: () -> Unit,
    onToggleAutoSave: () -> Unit,
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current

    var notificationUsage by remember { mutableStateOf<NotificationUsage?>(null) }

    val languageDialog = rememberBottomSheetDismissibleState()
    val notificationDialog = rememberDismissible()

    val notificationResult: (Boolean, Boolean) -> Unit = { granted, canRequestAgain ->
        if (granted) {
            if (notificationUsage == Notification) {
                onToggleNotification()
            }
            if (notificationUsage == AutoSave) {
                onToggleAutoSave()
            }
        } else if (!canRequestAgain) {
            onNotificationDeniedPermanently()
        }
    }

    val notificationPermission = rememberNotificationPermission(notificationResult)


    DialogNotificationPermission(
        state = notificationDialog,
        permissionState = notificationPermission,
        isPermanentlyDenied = notificationDeniedPermanently,
        onResult = notificationResult,
    )

    DialogLanguage(
        state = languageDialog,
        initialLanguage = language,
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.settings),
                navigationIcon = AppIcons.Navigation,
                onNavigationClick = onNavigateUp,
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {

            SettingsCard {
                SettingsButton(
                    checked = notificationEnable,
                    title = stringResource(R.string.notification),
                    description = stringResource(R.string.get_alerts_for_new_status_updates),
                    onClick = {
                        if (notificationPermission.status.isGranted) {
                            onToggleNotification()
                        } else {
                            notificationUsage = Notification
                            notificationDialog.show()
                        }
                    }
                )

                SettingsButton(
                    title = stringResource(R.string.auto_save),
                    checked = autoSaveEnable,
                    description = stringResource(R.string.save_new_statuses_automatically),
                    onClick = {
                        if (notificationPermission.status.isGranted) {
                            onToggleAutoSave()
                        } else {
                            notificationUsage = AutoSave
                            notificationDialog.show()
                        }
                    }
                )

                SettingsButton(
                    title = stringResource(R.string.download_location),
                    description = "/storage/Download/SavedStatus",
                )
            }

            SettingsCard {
                SettingsButton(
                    title = stringResource(R.string.language_options),
                    description = language?.name ?: stringResource(R.string._default),
                    icon = AppIcons.Language,
                    onClick = { languageDialog.show() }
                )
            }

            SettingsCard {
                VerticalSpacer(4.dp)

                SettingsButton(
                    title = stringResource(R.string.feedback_or_suggestion),
                    icon = AppIcons.Feedback,
                    onClick = {
                        IntentUtils.sendMail(context)
                    }
                )

                SettingsButton(
                    title = stringResource(R.string.rate_us),
                    icon = AppIcons.Like,
                    onClick = {
                        launchInAppReview(context.findActivity())
                    }
                )

                /*SettingsButton(
                    title = stringResource(R.string.share_app),
                    icon = AppIcons.Share,
                )*/

                VerticalSpacer(8.dp)
            }

            SettingsCard {
                VerticalSpacer(4.dp)

                SettingsButton(
                    title = stringResource(R.string.privacy_policy),
                    onClick = {
                        IntentUtils.browseUrl(context, AppDetails.PRIVACY_POLICY_URL)
                    }
                )

                /*SettingsButton(
                    title = stringResource(R.string.more_apps),
                    onClick = {},
                )*/

                SettingsButton(
                    title = stringResource(R.string.about),
                    description = "${stringResource(R.string.version)}: ${BuildConfig.VERSION_NAME}",
                )

                VerticalSpacer(4.dp)
            }

            VerticalSpacer(54.dp)
        }
    }
}

@Composable
fun SettingsButton(
    title: String,
    description: String,
    checked: Boolean,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    SettingsButton(
        modifier = modifier,
        title = title,
        description = description,
        onClick = onClick,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = null,
            )
        }
    )
}

@Composable
fun SettingsButton(
    title: String,
    icon: AppIcon,
    modifier: Modifier = Modifier,
    description: String? = null,
    onClick: (() -> Unit)? = null,
) {
    SettingsButton(
        modifier = modifier,
        title = title,
        description = description,
        onClick = onClick,
        leadingContent = {
            AppIcon(
                icon = icon,
                modifier = Modifier.padding(end = 8.dp),
            )
        }
    )
}

@Composable
fun SettingsButton(
    title: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    onClick: (() -> Unit)? = null,
    leadingContent: (@Composable () -> Unit)? = null,
    trailingContent: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                enabled = onClick != null,
                onClick = { onClick?.invoke() }
            )
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingContent?.invoke()

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (description != null) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = description,
                    style = typography.bodyMedium,
                    color = colorScheme.onSurfaceVariant,
                )
            }
        }

        trailingContent?.invoke()
    }
}

@Composable
fun SettingsCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(colorScheme.surfaceContainer),
        content = content,
    )
}
