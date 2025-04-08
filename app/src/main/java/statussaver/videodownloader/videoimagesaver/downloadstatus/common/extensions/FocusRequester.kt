package statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions

import androidx.compose.ui.focus.FocusRequester
import statussaver.videodownloader.videoimagesaver.downloadstatus.common.utils.runNonFatal

fun FocusRequester.safeRequestFocus() {
    runNonFatal("request_focus") { requestFocus() }
}