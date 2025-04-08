package statussaver.videodownloader.videoimagesaver.downloadstatus.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.statussaver.permission.PermissionState
import com.statussaver.permission.isDenied
import com.statussaver.permission.rememberNotificationPermission
import com.statussaver.permission.rememberStatusPermission
import kotlinx.coroutines.launch
import statussaver.videodownloader.videoimagesaver.downloadstatus.BuildConfig
import statussaver.videodownloader.videoimagesaver.downloadstatus.R
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.addIf
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions.findActivity
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.CollapsableBox
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogFolderPermission
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogHowToUse
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogNotificationPermission
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.dialogs.DialogStoragePermission
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.exitUntilCollapsedScrollBehavior
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberBottomSheetDismissibleState
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberDismissible
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui.rememberDismissibleSavable
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.IntentUtils
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.isAndroidMin11
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.launchInAppReview
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.Status
import statussaver.videodownloader.videoimagesaver.downloadstatus.data.StatusResult
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppIcon
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppImageButton
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.AppTopBar
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.components.VerticalSpacer
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.icons.AppIcons
import statussaver.videodownloader.videoimagesaver.downloadstatus.designsystem.theme.StatusSaverTheme
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.feed.SavedStatusFeedScreen
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.feed.WaStatusFeedScreen
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.home.HomeTab.SAVED
import statussaver.videodownloader.videoimagesaver.downloadstatus.ui.home.HomeTab.STATUS

enum class HomeTab { STATUS, SAVED }

@Composable
internal fun HomeScreenContent(
    whatsappStatusResult: StatusResult,
    savedStatusResult: StatusResult,
    notificationDeniedPermanently: Boolean,
    storageDeniedPermanently: Boolean,
    isSaving: (Status) -> Boolean,
    onStatusClick: (Status) -> Unit,
    onSaveClick: (Status) -> Unit,
    onDirectChatClick: () -> Unit,
    onBeingSpiedClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onEnableNotification: () -> Unit,
    onNotificationDeniedPermanently: () -> Unit,
    onStorageDeniedPermanently: () -> Unit,
) {
    val tabs = remember { HomeTab.entries.toList() }
    val pagerState = rememberPagerState { tabs.size }
    val selectedTab = remember(pagerState.currentPage) { tabs[pagerState.currentPage] }
    val scope = rememberCoroutineScope()

    val permissionState = rememberStatusPermission { granted, canRequestAgain ->
        if (!granted && !canRequestAgain) {
            onStorageDeniedPermanently()
        }
    }

    val scrollBehavior = exitUntilCollapsedScrollBehavior()
    val drawer = rememberDrawerState(DrawerValue.Closed)

    if (isAndroidMin11()) {
        val permissionBottomSheet = rememberBottomSheetDismissibleState(
            visible = permissionState.status.isDenied,
        )

        DialogFolderPermission(
            state = permissionBottomSheet,
            permissionState = permissionState,
        )
    } else {
        val permissionDialog = rememberDismissibleSavable(
            visible = permissionState.status.isDenied,
        )

        DialogStoragePermission(
            state = permissionDialog,
            permissionState = permissionState,
            storageDeniedPermanently = storageDeniedPermanently,
        )
    }

    DrawerScaffold(
        drawerState = drawer,
        scrollConnection = scrollBehavior.nestedScrollConnection,
        topBar = {
            HomeTopBar(
                notificationDeniedPermanently = notificationDeniedPermanently,
                onEnableNotification = onEnableNotification,
                onNotificationDeniedPermanently = onNotificationDeniedPermanently,
                onMenuClick = {
                    scope.launch { drawer.open() }
                },
            )
        },
        drawerContent = {
            DrawerContent(
                onSettingsClick = onSettingsClick,
                onBeingSpiedClick = onBeingSpiedClick,
            )
        }
    ) {
        CollapsableBox(scrollBehavior.state) {
            HomeActionButtonRow(
                onDirectChatClick = onDirectChatClick,
                onBeingSpiedClick = onBeingSpiedClick,
            )
        }

        HomeTabRow(
            tabs = tabs,
            selected = selectedTab,
            onSelected = { index ->
                scope.launch { pagerState.animateScrollToPage(index) }
            }
        )

        HomePager(
            modifier = Modifier.weight(1f),
            pagerState = pagerState,
            tabs = tabs,
            permissionState = permissionState,
            storageDeniedPermanently = storageDeniedPermanently,
            whatsappStatusResult = whatsappStatusResult,
            savedStatusResult = savedStatusResult,
            isSaving = isSaving,
            onSaveClick = onSaveClick,
            onStatusClick = onStatusClick,
        )
    }
}

@Composable
fun DrawerScaffold(
    drawerState: DrawerState,
    scrollConnection: NestedScrollConnection,
    drawerContent: @Composable ColumnScope.() -> Unit,
    topBar: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.systemBarsPadding(),
                drawerState = drawerState,
                content = drawerContent,
                drawerContainerColor = colorScheme.surfaceContainer,
                drawerContentColor = colorScheme.onSurface,
                windowInsets = WindowInsets(0),
            )
        },
    ) {
        Scaffold(
            modifier = Modifier.nestedScroll(scrollConnection),
            topBar = topBar,
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                content = content,
            )
        }
    }
}

@Composable
fun DrawerContent(
    onSettingsClick: () -> Unit,
    onBeingSpiedClick: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .verticalScroll(rememberScrollState())
            .widthIn(min = 320.dp)
            .width(IntrinsicSize.Max),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(colorScheme.surfaceContainerLow)
                .padding(start = 24.dp, end = 24.dp, top = 80.dp),
        ) {
            Text(
                text = stringResource(R.string.app_name),
                style = typography.titleLarge,
            )
        }

        VerticalSpacer(12.dp)

        AppDrawerItem(
            icon = AppIcons.Settings,
            text = stringResource(R.string.settings),
            onClick = onSettingsClick,
        )

        AppDrawerItem(
            icon = AppIcons.Spy,
            text = stringResource(R.string.am_i_being_watched),
            onClick = onBeingSpiedClick,
        )

        AppDrawerItem(
            icon = AppIcons.Feedback,
            text = stringResource(R.string.feedback),
            onClick = { IntentUtils.sendMail(context) },
        )

        AppDrawerItem(
            icon = AppIcons.Like,
            text = stringResource(R.string.rate_us),
            onClick = { launchInAppReview(context.findActivity()) },
        )

        /*AppDrawerItem(
            icon = AppIcons.ShareApp,
            text = stringResource(R.string.share_app),
            onClick = {},
        )*/

        Spacer(Modifier.weight(1f))

        Text(
            text = "${stringResource(R.string.version)}: ${BuildConfig.VERSION_NAME}",
            style = typography.bodyLarge,
            color = colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 32.dp),
        )
    }
}

@Composable
fun AppDrawerItem(
    icon: AppIcon,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                role = Role.Button,
            )
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppIcon(
            icon = icon,
            modifier = Modifier.size(26.dp),
        )
        Text(
            text = text,
            style = typography.labelLarge,
        )
    }
}

@Composable
private fun HomePager(
    pagerState: PagerState,
    tabs: List<HomeTab>,
    permissionState: PermissionState,
    storageDeniedPermanently: Boolean,
    whatsappStatusResult: StatusResult,
    savedStatusResult: StatusResult,
    isSaving: (Status) -> Boolean,
    onSaveClick: (Status) -> Unit,
    onStatusClick: (Status) -> Unit,
    modifier: Modifier = Modifier,
) {
    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        beyondViewportPageCount = 1,
    ) { index ->
        val tab = tabs[index]

        when (tab) {
            STATUS -> {
                WaStatusFeedScreen(
                    result = whatsappStatusResult,
                    permissionState = permissionState,
                    storageDeniedPermanently = storageDeniedPermanently,
                    onStatusClick = onStatusClick,
                    onSaveClick = onSaveClick,
                    isSaving = isSaving,
                )
            }

            SAVED -> {
                SavedStatusFeedScreen(
                    result = savedStatusResult,
                    onStatusClick = onStatusClick,
                    onSaveClick = onSaveClick,
                )
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    notificationDeniedPermanently: Boolean,
    onMenuClick: () -> Unit,
    onEnableNotification: () -> Unit,
    onNotificationDeniedPermanently: () -> Unit,
) {
    val context = LocalContext.current

    val notificationResult: (Boolean, Boolean) -> Unit = { granted, canRequestAgain ->
        if (granted) {
            onEnableNotification()
        } else if (!canRequestAgain) {
            onNotificationDeniedPermanently()
        }
    }

    val notificationPermission = rememberNotificationPermission(notificationResult)
    val notificationDialog = rememberDismissible()
    val howToUseDialog = rememberBottomSheetDismissibleState()

    DialogNotificationPermission(
        state = notificationDialog,
        permissionState = notificationPermission,
        isPermanentlyDenied = notificationDeniedPermanently,
        onResult = notificationResult,
    )

    DialogHowToUse(howToUseDialog)

    AppTopBar(
        title = stringResource(R.string.app_name),
        navigationIcon = AppIcons.Menu,
        onNavigationClick = onMenuClick,
        actions = {
            AppImageButton(
                image = AppIcons.Whatsapp,
                onClick = { IntentUtils.openInstalledWhatsapp(context) },
            )

            if (notificationPermission.status.isDenied) {
                AppImageButton(
                    image = AppIcons.NotificationAlert,
                    onClick = notificationDialog::show,
                )
            } else {
                AppImageButton(
                    image = AppIcons.Info,
                    onClick = howToUseDialog::show,
                )
            }
        }
    )
}

@Composable
private fun HomeActionButtonRow(
    onDirectChatClick: () -> Unit,
    onBeingSpiedClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        HomeActionButton(
            icon = AppIcons.DirectChat,
            text = stringResource(R.string.direct_chat),
            onClick = onDirectChatClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
        HomeActionButton(
            icon = AppIcons.Spy,
            text = stringResource(R.string.am_i_being_spied),
            marquee = false,
            onClick = onBeingSpiedClick,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Composable
private fun HomeTabRow(
    tabs: List<HomeTab>,
    selected: HomeTab,
    onSelected: (index: Int) -> Unit,
) {
    val selectedTabIndex = tabs.indexOf(selected)
    val isRtl = LocalLayoutDirection.current == LayoutDirection.Rtl

    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier = Modifier
                    .addIf(isRtl) {
                        scale(-1f, 1f)
                    }
                    .tabIndicatorOffset(selectedTabIndex, true),
                width = Dp.Unspecified,
            )
        }
    ) {
        tabs.fastForEach { tab ->
            HomeTab(
                tab = tab,
                selected = tab == selected,
                onClick = { onSelected(tabs.indexOf(tab)) },
            )
        }
    }
}

@Composable
private fun HomeActionButton(
    icon: AppIcon,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    marquee: Boolean = false,
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(),
        colors = ButtonDefaults.buttonColors(
            containerColor = colorScheme.surfaceContainer,
            contentColor = colorScheme.onSurface,
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AppIcon(icon)

            Text(
                text = text,
                style = typography.labelLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .addIf(marquee) {
                        basicMarquee(
                            iterations = 6,
                            repeatDelayMillis = 2_500,
                        )
                    }
            )
        }
    }
}

@Composable
private fun HomeTab(
    tab: HomeTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Tab(
        modifier = modifier,
        selected = selected,
        onClick = onClick,
        selectedContentColor = colorScheme.primary,
        unselectedContentColor = colorScheme.onSurfaceVariant,
    ) {
        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = homeTabTitle(tab),
            style = typography.titleSmall
        )
    }
}

@Composable
private fun homeTabTitle(tab: HomeTab): String {
    return when (tab) {
        STATUS -> stringResource(R.string.status)
        SAVED -> stringResource(R.string.saved)
    }
}

@Composable
@Preview(showBackground = true)
fun HomeScreenPreview() {
    StatusSaverTheme {
        //HomeScreenContent()
    }
}