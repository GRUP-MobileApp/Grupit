package com.grup.ui.apptheme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

internal data class AppDimensions(
    val appPadding: Dp = 15.dp,
    val cardPadding: Dp = 20.dp,

    val rowCardPadding: Dp = 15.dp,
    val itemRowCardHeight: Dp = 60.dp,
    val bigItemRowCardHeight: Dp = 90.dp,

    val paddingSmall: Dp = 5.dp,
    val paddingMedium: Dp = 10.dp,
    val paddingLarge: Dp = 20.dp,
    val paddingExtraLarge: Dp = 30.dp,

    val spacingExtraSmall: Dp = 2.dp,
    val spacingSmall: Dp = 5.dp,
    val spacing: Dp = 10.dp,
    val spacingMedium: Dp = 15.dp,
    val spacingLarge: Dp = 20.dp,
    val spacingExtraLarge: Dp = 30.dp,

    val topBarSize: Float = 0.07f,
    val shadowElevationSize: Dp = 6.dp,

    val tinyIconSize: Dp = 20.dp,
    val smallIconSize: Dp = 30.dp,
    val iconSize: Dp = 50.dp,
    val largeIconSize: Dp = 72.dp,

    val actionCardSize: Dp = 150.dp
)

internal val defaultDimensions = AppDimensions()

internal val LocalDimensions = staticCompositionLocalOf { defaultDimensions }
