package com.grup.ui.apptheme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

internal data class AppDimensions(
    val appPadding: Dp = 15.dp,
    val cardPadding: Dp = 20.dp,

    val rowCardPadding: Dp = 15.dp,

    val paddingSmall: Dp = 5.dp,
    val paddingMedium: Dp = 10.dp,
    val paddingLarge: Dp = 20.dp,
    val paddingExtraLarge: Dp = 30.dp,

    val spacingSmall: Dp = 5.dp,
    val spacing: Dp = 10.dp,
    val spacingMedium: Dp = 15.dp,
    val spacingLarge: Dp = 20.dp,
    val spacingExtraLarge: Dp = 30.dp,

    val groupDetailsSize: Dp = 350.dp,
    val topBarSize: Float = 0.07f,
    val smallIconSize: Dp = 30.dp,
    val smallButtonSize: Dp = 24.dp,
    val shadowElevationSize: Dp = 6.dp,

    val smallFont: TextUnit = 20.sp
)

internal val defaultDimensions = AppDimensions()
internal val smallDimensions = AppDimensions(
    smallButtonSize = 16.dp
)

internal val LocalDimensions = staticCompositionLocalOf { defaultDimensions }