package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.language

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.shady.language.Language
import com.shady.language.Languages
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppTopBar
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons


@Composable
fun LanguageScreenContent(
    onNavigateForward: (Language?) -> Unit,
) {
    val languages = Languages.common

    var selectedLanguage by remember { mutableStateOf<Language?>(null) }


    Scaffold(
        topBar = {
            LanguageTopBar(
                onNavigateForward = { onNavigateForward(selectedLanguage) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 54.dp)
        ) {

            item {
                LanguageItem(
                    title = stringResource(R.string._default),
                    selected = selectedLanguage == null,
                    onClick = { selectedLanguage = null },
                    leadingContent = {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = languages[0].flagEmoji,
                                style = typography.titleMedium,
                                color = Color.Transparent,
                            )
                            AppIcon(AppIcons.Language)
                        }
                    }
                )
            }

            items(
                items = languages,
                key = { it.code }
            ) { language ->
                LanguageItem(
                    language = language,
                    selected = selectedLanguage == language,
                    onClick = { selectedLanguage = language }
                )
            }
        }
    }
}

@Composable
fun LanguageTopBar(
    onNavigateForward: () -> Unit,
) {
    AppTopBar(
        title = stringResource(R.string.select_language),
    ) {
        FinishButton(onClick = onNavigateForward)
    }
}

@Composable
fun LanguageItem(
    language: Language,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LanguageItem(
        title = language.name,
        selected = selected,
        onClick = onClick,
        leadingContent = {
            Text(
                text = language.flagEmoji,
                style = typography.titleMedium,
            )
        },
        modifier = modifier,
    )
}

@Composable
fun LanguageItem(
    title: String,
    selected: Boolean,
    onClick: () -> Unit,
    leadingContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
            .background(if (selected) colorScheme.primary else colorScheme.surfaceContainer)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leadingContent()

        Text(
            text = title,
            style = typography.labelLarge,
        )
    }
}

@Composable
fun FinishButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .padding(end = 8.dp)
            .clip(RoundedCornerShape(100.dp))
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
            .background(colorScheme.primary)
            .padding(horizontal = 20.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIcon(
            icon = AppIcons.Check,
            tint = colorScheme.onPrimary
        )
    }
}