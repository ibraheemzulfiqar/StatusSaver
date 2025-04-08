package statussaver.videodownloader.videoimagesaver.downloadstatus.common.ui

import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.animation.core.spring
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun CollapsableBox(
    state: CollapsableState,
    modifier: Modifier = Modifier,
    content: @Composable @UiComposable () -> Unit,
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurable, constrains ->
        val placeable = measurable.fastMap { it.measure(constrains) }
        val layoutWidth = placeable.fastMaxBy { it.width }?.width ?: 0
        val actualHeight = placeable.fastMaxBy { it.height }?.height ?: 0

        val layoutHeight =
            (actualHeight + state.heightOffset.roundToInt()).coerceIn(0, actualHeight)

        if (state.heightOffsetLimit == -Float.MAX_VALUE) {
            state.heightOffsetLimit = -actualHeight.toFloat()
        }
        layout(layoutWidth, layoutHeight) {
            placeable.fastForEach {
                it.placeRelative(layoutWidth - it.width, layoutHeight - it.height)
            }
        }
    }
}

@Composable
fun exitUntilCollapsedScrollBehavior(
    state: CollapsableState = rememberCollapsableState(),
    canScroll: () -> Boolean = { true },
    snapAnimationSpec: AnimationSpec<Float>? = spring(stiffness = Spring.StiffnessMediumLow),
    flingAnimationSpec: DecayAnimationSpec<Float>? = rememberSplineBasedDecay()
): ExitUntilCollapsedScrollBehavior =
    ExitUntilCollapsedScrollBehavior(
        state = state,
        snapAnimationSpec = snapAnimationSpec,
        flingAnimationSpec = flingAnimationSpec,
        canScroll = canScroll,
    )

@Composable
fun rememberCollapsableState(
    initialHeightOffsetLimit: Float = -Float.MAX_VALUE,
    initialHeightOffset: Float = 0f,
    initialContentOffset: Float = 0f
): CollapsableState {
    return rememberSaveable(saver = CollapsableState.Saver) {
        CollapsableState(initialHeightOffsetLimit, initialHeightOffset, initialContentOffset)
    }
}

class CollapsableState(
    initialHeightOffsetLimit: Float,
    initialHeightOffset: Float,
    initialContentOffset: Float
) {

    var heightOffsetLimit by mutableFloatStateOf(initialHeightOffsetLimit)

    var heightOffset: Float
        get() = _heightOffset.floatValue
        set(newOffset) {
            _heightOffset.floatValue =
                newOffset.coerceIn(minimumValue = heightOffsetLimit, maximumValue = 0f)
        }

    var contentOffset by mutableFloatStateOf(initialContentOffset)

    /**
     * A value that represents the collapsed height percentage of the collapsable view.
     *
     * A `0.0` represents a fully expanded bar, and `1.0` represents a fully collapsed bar (computed
     * as [heightOffset] / [heightOffsetLimit]).
     */
    val collapsedFraction: Float
        get() =
            if (heightOffsetLimit != 0f) {
                heightOffset / heightOffsetLimit
            } else {
                0f
            }

    /**
     * A value that represents the percentage of the collapsable bar area that is overlapping with the
     * content scrolled behind it.
     *
     * A `0.0` indicates that the app bar does not overlap any content, while `1.0` indicates that
     * the entire visible app bar area overlaps the scrolled content.
     */
    val overlappedFraction: Float
        get() =
            if (heightOffsetLimit != 0f) {
                1 - ((heightOffsetLimit - contentOffset).coerceIn(
                    minimumValue = heightOffsetLimit,
                    maximumValue = 0f
                ) / heightOffsetLimit)
            } else {
                0f
            }

    companion object {
        val Saver: Saver<CollapsableState, *> =
            listSaver(
                save = { listOf(it.heightOffsetLimit, it.heightOffset, it.contentOffset) },
                restore = {
                    CollapsableState(
                        initialHeightOffsetLimit = it[0],
                        initialHeightOffset = it[1],
                        initialContentOffset = it[2]
                    )
                }
            )
    }

    private var _heightOffset = mutableFloatStateOf(initialHeightOffset)
}

class ExitUntilCollapsedScrollBehavior(
    val state: CollapsableState,
    val snapAnimationSpec: AnimationSpec<Float>?,
    val flingAnimationSpec: DecayAnimationSpec<Float>?,
    val canScroll: () -> Boolean = { true }
) {

    val nestedScrollConnection = object : NestedScrollConnection {
        override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
            // Don't intercept if scrolling down.
            if (!canScroll() || available.y > 0f) return Offset.Zero

            val prevHeightOffset = state.heightOffset
            state.heightOffset = state.heightOffset + available.y
            return if (prevHeightOffset != state.heightOffset) {
                // We're in the middle of top app bar collapse or expand.
                // Consume only the scroll on the Y axis.
                available.copy(x = 0f)
            } else {
                Offset.Zero
            }
        }

        override fun onPostScroll(
            consumed: Offset,
            available: Offset,
            source: NestedScrollSource
        ): Offset {
            if (!canScroll()) return Offset.Zero

            state.contentOffset += consumed.y

            if (available.y < 0f || consumed.y < 0f) {
                // When scrolling up, just update the state's height offset.
                val oldHeightOffset = state.heightOffset
                state.heightOffset = state.heightOffset + consumed.y
                return Offset(0f, state.heightOffset - oldHeightOffset)
            }

            if (consumed.y == 0f && available.y > 0) {
                // Reset the total content offset to zero when scrolling all the way down. This
                // will eliminate some float precision inaccuracies.
                state.contentOffset = 0f
            }

            if (available.y > 0f) {
                // Adjust the height offset in case the consumed delta Y is less than what was
                // recorded as available delta Y in the pre-scroll.
                val oldHeightOffset = state.heightOffset
                state.heightOffset = state.heightOffset + available.y
                return Offset(0f, state.heightOffset - oldHeightOffset)
            }
            return Offset.Zero
        }

        override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
            val superConsumed = super.onPostFling(consumed, available)
            return superConsumed +
                    settleAppBar(state, available.y, flingAnimationSpec, snapAnimationSpec)
        }
    }

    suspend fun expand() {
        animateHeightOffset(0f)
    }

    suspend fun collapse() {
        animateHeightOffset(state.heightOffsetLimit)
    }

    private suspend fun animateHeightOffset(targetValue: Float) {
        if (snapAnimationSpec != null) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                targetValue = targetValue,
                animationSpec = snapAnimationSpec
            ) {
                state.heightOffset = value
            }
        } else {
            state.heightOffset = targetValue
        }
    }
}

private suspend fun settleAppBar(
    state: CollapsableState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    // Check if the app bar is completely collapsed/expanded. If so, no need to settle the app bar,
    // and just return Zero Velocity.
    // Note that we don't check for 0f due to float precision with the collapsedFraction
    // calculation.
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    // In case there is an initial velocity that was left after a previous user fling, animate to
    // continue the motion to expand or collapse the app bar.
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // avoid rounding errors and stop if anything is unconsumed
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    // Snap if animation specs were provided.
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 && state.heightOffset > state.heightOffsetLimit) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec
            ) {
                state.heightOffset = value
            }
        }
    }

    return Velocity(0f, remainingVelocity)
}