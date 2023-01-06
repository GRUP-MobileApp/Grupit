package com.grup.android.ui.apptheme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class AppDimensions(
    val paddingSmall: Dp = 4.dp,
    val paddingMedium: Dp = 10.dp,
    val paddingLarge: Dp = 20.dp,
    val paddingExtraLarge: Dp = 30.dp,
    val smallSpacing: Dp = 10.dp,
    val spacing: Dp = 20.dp,

    val groupDetailsSize: Dp = 350.dp,
    val topBarSize: Float = 0.07f,
    val iconSize: Dp = 30.dp,
    val borderIconSize: Dp = iconSize + 15.dp,
    val shadowElevationSize: Dp = 6.dp,

    val smallFont: TextUnit = 20.sp
)

internal val LocalDimensions = staticCompositionLocalOf { AppDimensions() }