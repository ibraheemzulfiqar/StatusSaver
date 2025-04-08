package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.shady.language.Language
import com.shady.language.LanguageManager
import com.shady.language.Languages
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.LocalAnalyticsHelper
import statussaver.videodownloader.videoimagesaver.downloadstatus.analytics.logEvent
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.BottomSheetDismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dismissed
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButton
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.MainActivity

@Composable
fun DialogLanguage(
    state: BottomSheetDismissibleState,
    initialLanguage: Language?,
) {
    if (state.dismissed) return

    val context = LocalContext.current
    val analytics = LocalAnalyticsHelper.current

    val languageManager = LanguageManager.getInstance()

    val languages = remember {
        val languages = Languages.common.toMutableList()
        val currentIndex = languages.indexOfFirst { it.code == initialLanguage?.code }

        if (currentIndex >= 0) {
            languages.add(0, languages.removeAt(currentIndex))
        }

        languages
    }
    var selectedLanguage by remember { mutableStateOf(initialLanguage) }

    AppBottomSheet(
        state = state,
        eventName = "Language"
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.select_language),
                style = typography.headlineLarge,
                modifier = Modifier.padding(24.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.76f),
            ) {
                item {
                    LanguageItem(
                        title = stringResource(R.string._default),
                        selected = selectedLanguage == null,
                        onClick = { selectedLanguage = null }
                    )
                }

                items(
                    items = languages,
                    key = { it.code }
                ) { language ->
                    LanguageItem(
                        title = language.name,
                        selected = selectedLanguage == language,
                        onClick = { selectedLanguage = language }
                    )
                }
            }

            AppButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                text = stringResource(R.string.okay),
                onClick = {
                    state.dismiss()

                    if (selectedLanguage != initialLanguage) {
                        languageManager.setLanguage(context, selectedLanguage)
                        MainActivity.restart(context)
                    }

                    analytics.logEvent(
                        name = "language_dialog",
                        "action" to "Okay",
                        "selectedLanguage" to selectedLanguage?.name,
                        "initialLanguage" to initialLanguage?.name,
                    )
                }
            )
        }
    }
}

@Composable
private fun LanguageItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = title,
            style = typography.labelLarge,
            modifier = Modifier.weight(1f),
        )

        if (selected) {
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .background(colorScheme.onPrimary)
                )
                AppIcon(
                    icon = AppIcons.CheckBox,
                    tint = colorScheme.primary,
                    modifier = Modifier.size(24.dp),
                )
            }
        } else {
            AppIcon(
                icon = AppIcons.CircleOutline,
                tint = colorScheme.onSurfaceVariant,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}