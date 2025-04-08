package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.directchat

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.addIf
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.BottomSheetDismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.Country
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.AppBottomSheet
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dismissed
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.getSimCountryCode
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberBottomSheetDismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberCountries
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.IntentUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.PackageManagerUtils.WHATSAPP_BUSINESS_PACKAGE_NAME
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.PackageManagerUtils.WHATSAPP_PACKAGE_NAME
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.PackageManagerUtils.isAppInstalled
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppButton
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppTopBar
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.StatusSaverTheme
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.directchat.DirectChat.WHATSAPP
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.directchat.DirectChat.WHATSAPP_BUSINESS

@Preview
@Composable
private fun DirectChatScreenContentPrev() {
    StatusSaverTheme {
        DirectChatScreenContent({})
    }
}

enum class DirectChat { WHATSAPP, WHATSAPP_BUSINESS }

@Composable
fun DirectChatScreenContent(
    onNavigateUp: () -> Unit,
) {
    val context = LocalContext.current
    val inspectionMode = LocalInspectionMode.current

    val countries = rememberCountries()
    val simCountryCode = remember { getSimCountryCode(context, countries) }

    val isWhatsAppInstalled = remember {
        inspectionMode || isAppInstalled(context, WHATSAPP_PACKAGE_NAME)
    }
    val isWhatsappBusinessInstalled = remember {
        inspectionMode || isAppInstalled(context, WHATSAPP_BUSINESS_PACKAGE_NAME)
    }

    var phoneNumber by rememberSaveable { mutableStateOf("") }
    var phoneNumberCode by rememberSaveable { mutableStateOf(simCountryCode) }
    var message by rememberSaveable { mutableStateOf("") }

    var directChat by remember { mutableStateOf(if (isWhatsAppInstalled) WHATSAPP else WHATSAPP_BUSINESS) }

    var countriesDialog = rememberBottomSheetDismissibleState()

    CountryCodeDialog(
        state = countriesDialog,
        countries = countries,
        onCodeSelected = { phoneNumberCode = it }
    )

    Scaffold(
        topBar = {
            AppTopBar(
                title = stringResource(R.string.direct_chat),
                navigationIcon = AppIcons.Navigation,
                onNavigationClick = onNavigateUp,
            )
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
                .animateContentSize()
        ) {
            if (isWhatsAppInstalled && isWhatsappBusinessInstalled) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconSelectorButton(
                        modifier = Modifier.weight(1f),
                        icon = AppIcons.Whatsapp,
                        selected = directChat == WHATSAPP,
                        onClick = {
                            directChat = WHATSAPP
                        },
                    )
                    IconSelectorButton(
                        modifier = Modifier.weight(1f),
                        icon = AppIcons.WhatsappBusiness,
                        selected = directChat == WHATSAPP_BUSINESS,
                        onClick = {
                            directChat = WHATSAPP_BUSINESS
                        },
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(100))
                        .clickable(
                            role = Role.Button,
                            interactionSource = null,
                            indication = ripple(),
                            onClick = { countriesDialog.show() }
                        )
                        .background(colorScheme.surfaceContainer)
                        .padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CompositionLocalProvider(
                        LocalLayoutDirection provides LayoutDirection.Ltr
                    ) {
                        Text(text = phoneNumberCode)
                        AppIcon(AppIcons.ArrowDown)
                    }
                }

                AppTextField(
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = stringResource(R.string.phone_number),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Next,
                    ),
                    shape = RoundedCornerShape(100),
                )
            }

            Spacer(Modifier.height(12.dp))

            AppTextField(
                value = message,
                onValueChange = { message = it },
                placeholder = stringResource(R.string.please_enter_something),
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.height(16.dp))

            AppButton(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.send),
                enabled = phoneNumber.isNotEmpty(),
                onClick = {
                    IntentUtils.sendDirectMessage(
                        context = context,
                        number = phoneNumberCode + phoneNumber,
                        message = message,
                        packageName = when (directChat) {
                            WHATSAPP -> WHATSAPP_PACKAGE_NAME
                            WHATSAPP_BUSINESS -> WHATSAPP_BUSINESS_PACKAGE_NAME
                        },
                    )
                },
            )

            Spacer(Modifier.height(16.dp))
        }
    }
}

@Composable
fun IconSelectorButton(
    icon: AppIcon,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(100))
            .clickable(
                onClick = onClick,
                role = Role.Button,
                interactionSource = null,
                indication = ripple(),
            )
            .addIf(selected) {
                background(colorScheme.primary)
            }
            .addIf(!selected) {
                border(1.dp, colorScheme.primary, RoundedCornerShape(100))
            }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            icon = icon,
            tint = if (selected) colorScheme.onPrimary else colorScheme.primary,
        )
    }
}

@Composable
fun AppTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    placeholderColor: Color = colorScheme.onSurfaceVariant,
    shape: Shape = RoundedCornerShape(16.dp),
    containerColor: Color = colorScheme.surfaceContainer,
    contentColor: Color = colorScheme.onSurface,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = false,
    textStyle: TextStyle = typography.bodyLarge.copy(
        color = contentColor,
    ),
) {
    BasicTextField(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(containerColor)
            .padding(contentPadding),
        value = value,
        onValueChange = onValueChange,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        cursorBrush = SolidColor(colorScheme.primary),
        singleLine = singleLine,
        textStyle = textStyle,
    ) { innerTextField ->
        Box {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    style = textStyle,
                    color = placeholderColor,
                )
            }

            innerTextField()
        }
    }
}

@Composable
fun CountryCodeDialog(
    state: BottomSheetDismissibleState,
    countries: List<Country>,
    onCodeSelected: (String) -> Unit,
) {
    if (state.dismissed) return

    var search by remember { mutableStateOf("") }

    val filteredCountries by remember(search) {
        countries.fastFilter { it.contains(search) }
            .sortedBy { it.country }
            .run { mutableStateOf(this) }
    }

    AppBottomSheet(
        state = state,
        eventName = "CountriesCode",
        dragHandle = { BottomSheetDefaults.DragHandle() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 250.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            SearchField(
                value = search,
                onValueChange = { search = it },
            )

            if (filteredCountries.isEmpty()) {
                Text(
                    text = stringResource(R.string.search_not_found),
                    modifier = Modifier.padding(16.dp),
                )
            } else {
                CountryFeed(
                    countries = filteredCountries,
                    onCodeSelected = {
                        onCodeSelected(it)
                        state.dismiss()
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = colorScheme.surface,
    contentColor: Color = colorScheme.onSurface,
) {
    Row(
        modifier = modifier
            .padding(16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(100))
            .background(containerColor),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIcon(
            icon = AppIcons.Search,
            modifier = Modifier
                .padding(start = 16.dp)
                .size(20.dp),
            tint = colorScheme.onSurfaceVariant,
        )

        AppTextField(
            placeholder = stringResource(R.string.search),
            value = value,
            shape = RectangleShape,
            contentPadding = PaddingValues(12.dp),
            singleLine = true,
            containerColor = containerColor,
            contentColor = contentColor,
            onValueChange = onValueChange,
        )
    }
}

@Composable
private fun CountryFeed(
    countries: List<Country>,
    onCodeSelected: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.87f),
    ) {
        items(
            items = countries,
            key = { "${it.code}-${it.country}" },
        ) { country ->

            CompositionLocalProvider(
                LocalLayoutDirection provides LayoutDirection.Ltr
            ) {
                CountryItem(
                    country = country,
                    onClick = { onCodeSelected(country.code) }
                )
            }
        }
    }
}

@Composable
private fun CountryItem(
    country: Country,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
            .padding(16.dp),
    ) {
        Text(
            text = country.emoji,
            modifier = Modifier.padding(end = 8.dp)
        )

        Text(
            text = country.country,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )

        Spacer(Modifier.weight(1f))

        Text(country.code)
    }
}