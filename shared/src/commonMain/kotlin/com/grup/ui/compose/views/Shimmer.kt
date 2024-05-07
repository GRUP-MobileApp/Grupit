package com.grup.ui.compose.views

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.IntSize

sealed class ShimmerState {
    data object Loading : ShimmerState()
    data object Complete : ShimmerState()
}

private val LocalShimmerState = staticCompositionLocalOf<ShimmerState> {
    error("No ShimmerState provided")
}

internal interface ShimmerScope {
    val shimmerTimeout: Int

    @Composable
    fun Modifier.shimmerModifier() = this.apply {
        if (LocalShimmerState.current == ShimmerState.Loading) {
            composed {
                var size by remember { mutableStateOf(IntSize.Zero) }
                val transition = rememberInfiniteTransition()
                val startOffsetX by transition.animateFloat(
                    initialValue = -2 * size.width.toFloat(),
                    targetValue = 2 * size.width.toFloat(),
                    animationSpec = infiniteRepeatable(
                        animation = tween(shimmerTimeout)
                    )
                )

                background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFB8B5B5),
                            Color(0xFF8F8B8B),
                            Color(0xFFB8B5B5),
                        ),
                        start = Offset(startOffsetX, 0f),
                        end = Offset(startOffsetX + size.width.toFloat(), size.height.toFloat())
                    )
                ).onGloballyPositioned { size = it.size }
            }
        }
    }
}

private class ShimmerScopeImpl(content: ShimmerScope.() -> Unit) : ShimmerScope {
    init {
        apply { content() }
    }
    override val shimmerTimeout: Int = 1000
}

@Composable
internal fun ShimmerLayout(state: ShimmerState, content: ShimmerScope.() -> Unit) {
    val shimmerState = rememberUpdatedState(state)

    CompositionLocalProvider(LocalShimmerState provides shimmerState.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            ShimmerScopeImpl(content)
        }
    }
}