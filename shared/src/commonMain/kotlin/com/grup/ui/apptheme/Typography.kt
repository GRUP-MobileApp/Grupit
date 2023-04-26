package com.grup.ui.apptheme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

expect val proxima_nova: FontFamily

internal data class AppTypography(
    val h1: TextStyle = TextStyle(
        fontFamily = proxima_nova,
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp
    ),
    val smallFont: TextStyle = TextStyle(
        fontFamily = proxima_nova,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    val button: TextStyle = TextStyle(
        fontFamily = proxima_nova,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

internal val LocalTypography = staticCompositionLocalOf { AppTypography() }
