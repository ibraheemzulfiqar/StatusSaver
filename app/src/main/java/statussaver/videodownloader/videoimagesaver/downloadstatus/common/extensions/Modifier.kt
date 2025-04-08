package statussaver.videodownloader.videoimagesaver.downloadstatus.common.extensions

import androidx.compose.ui.Modifier

inline fun Modifier.addIf(
    condition: Boolean,
    block: Modifier.() -> Modifier,
): Modifier {
    return if (condition) {
        then(Modifier.block())
    } else {
        this
    }
}