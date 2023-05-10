package com.grup.ui.apptheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.icerock.moko.resources.compose.fontFamilyResource
import com.grup.library.MR

internal class AppTypography {
    val h1: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = fontFamilyResource(MR.fonts.ProximaNova.regular),
            fontWeight = FontWeight.Normal,
            fontSize = 24.sp
        )
    val smallFont: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = fontFamilyResource(MR.fonts.ProximaNova.regular),
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp
        )
}

internal val LocalTypography = staticCompositionLocalOf { AppTypography() }
