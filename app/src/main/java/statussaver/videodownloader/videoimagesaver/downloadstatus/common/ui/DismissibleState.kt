package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui

import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

val DismissibleState.dismissed: Boolean
    get() = visible.not()

@Composable
fun rememberDismissible(
    visible: Boolean = false,
    onStateChange: ((Boolean) -> Unit)? = null,
): DismissibleState {
    return remember(Unit) { DismissibleState(visible, onStateChange) }
}

@Composable
fun rememberDismissibleSavable(
    visible: Boolean = false,
    onStateChange: ((Boolean) -> Unit)? = null,
): DismissibleState {
    return rememberSaveable(
        saver = DismissibleStateImpl.Saver(
            listener = onStateChange
        )
    ) {
        DismissibleState(visible, onStateChange)
    }
}

@Composable
fun rememberBottomSheetDismissibleState(
    visible: Boolean = false,
    skipPartiallyExpanded: Boolean = true,
    scope: CoroutineScope = rememberCoroutineScope(),
    confirmValueChange: (SheetValue) -> Boolean = { true },
    onStateChange: ((Boolean) -> Unit)? = null,
): BottomSheetDismissibleState {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = skipPartiallyExpanded,
        confirmValueChange = confirmValueChange,
    )

    return rememberSaveable(
        skipPartiallyExpanded,
        saver = BottomSheetDismissibleState.Saver(
            sheetState = sheetState,
            scope = scope,
            listener = onStateChange
        )
    ) {
        BottomSheetDismissibleState(
            sheetState = sheetState,
            initialValue = visible,
            scope = scope,
            listener = onStateChange
        )
    }
}


fun DismissibleState(
    visible: Boolean,
    onStateChange: ((Boolean) -> Unit)? = null,
): DismissibleState = DismissibleStateImpl(visible, onStateChange)

interface DismissibleState {

    val visible: Boolean

    fun show()

    fun dismiss()

}


private class DismissibleStateImpl(
    default: Boolean,
    private val listener: ((Boolean) -> Unit)? = null,
) : DismissibleState {

    override var visible: Boolean by mutableStateOf(default)
        private set

    override fun show() {
        visible = true
        listener?.invoke(true)
    }

    override fun dismiss() {
        visible = false
        listener?.invoke(false)
    }

    companion object {
        fun Saver(
            listener: ((Boolean) -> Unit)? = null,
        ) = Saver<DismissibleState, Boolean>(
            save = { it.visible },
            restore = { DismissibleStateImpl(it, listener) }
        )
    }
}

class BottomSheetDismissibleState(
    val sheetState: SheetState,
    private val initialValue: Boolean = sheetState.isVisible,
    private val scope: CoroutineScope,
    private val listener: ((Boolean) -> Unit)? = null,
) : DismissibleState {

    override var visible: Boolean by mutableStateOf(initialValue)
        private set

    init {
        if (visible) {
            show()
        } else {
            dismiss()
        }
    }

    override fun show() {
        scope.launch {
            visible = true
            sheetState.show()
            listener?.invoke(true)
        }
    }

    override fun dismiss() {
        scope.launch {
            sheetState.hide()
            visible = false
            listener?.invoke(false)
        }
    }

    companion object {

        fun Saver(
            sheetState: SheetState,
            scope: CoroutineScope,
            listener: ((Boolean) -> Unit)? = null,
        ) = Saver<BottomSheetDismissibleState, Boolean>(
            save = { it.visible },
            restore = {
                BottomSheetDismissibleState(
                    initialValue = it,
                    sheetState = sheetState,
                    scope = scope,
                    listener = listener,
                )
            }
        )
    }
}