package statussaver.videodownloader.videoimagesaver.downloadstatus.analytics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect


fun AnalyticsHelper.logScreenView(screenName: String, vararg extras: Pair<String, Any?>) {
    logEvent(
        name = AnalyticsEvent.Types.SCREEN_VIEW,
        AnalyticsEvent.ParamKeys.SCREEN_NAME to screenName,
        *extras,
    )
}

fun AnalyticsHelper.logDialogView(dialogName: String, vararg extras: Pair<String, Any?>) {
    logEvent(
        name = AnalyticsEvent.Types.DIALOG_VIEW,
        AnalyticsEvent.ParamKeys.DIALOG_NAME to dialogName,
        *extras,
    )
}

fun AnalyticsHelper.logEvent(name: String, vararg extras: Pair<String, Any?>) {
    logEvent(
        AnalyticsEvent(
            type = name,
            extras = extras.map { AnalyticsEvent.Param(it.first, it.second.toString()) },
        ),
    )
}


@Composable
fun TrackScreenViewEvent(
    name: String,
    vararg extras: Pair<String, Any?>,
) {
    val analyticsHelper = LocalAnalyticsHelper.current

    DisposableEffect(Unit) {
        analyticsHelper.logScreenView(name, *extras)
        onDispose {}
    }
}

@Composable
fun TrackDialogViewEvent(
    name: String,
    vararg extras: Pair<String, Any?>,
) {
    val analyticsHelper = LocalAnalyticsHelper.current

    DisposableEffect(Unit) {
        analyticsHelper.logDialogView(name, *extras)
        onDispose {}
    }
}