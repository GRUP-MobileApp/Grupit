package com.example.grup.android.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class AppDimensions(
    val paddingSmall: Dp = 4.dp,
    val paddingMedium: Dp = 8.dp,
    val paddingLarge: Dp = 20.dp,
    val paddingExtraLarge: Dp = 30.dp,
    val spacing: Dp = 20.dp,

    val groupDetailsSize: Dp = 330.dp,
    val topBarSize: Float = 0.07f,
    val iconSize: Dp = 30.dp,
    val divider: Dp = 3.dp
)

internal val LocalDimensions = staticCompositionLocalOf { AppDimensions() }