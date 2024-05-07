package com.grup.ui.apptheme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.grup.library.MR
import dev.icerock.moko.resources.compose.fontFamilyResource

internal class AppTypography {
    val h1: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = fontFamilyResource(MR.fonts.ProximaNova.regular),
            fontWeight = FontWeight.Normal
        )
    val caption: TextStyle
        @Composable
        get() = TextStyle(
            fontFamily = fontFamilyResource(MR.fonts.ProximaNova.regular),
            fontWeight = FontWeight.Normal
        )
    val tinyFont: TextUnit = 11.sp
    val smallFont: TextUnit = 13.sp
    val mediumFont: TextUnit = 16.sp
    val largeFont: TextUnit = 22.sp
    val extraLargeFont: TextUnit = 28.sp
    val headerFont: TextUnit = 20.sp
    val largeHeaderFont: TextUnit = 32.sp

    val textFieldFont: TextUnit = 20.sp

    val moneyAmountFont: TextUnit = 20.sp
    val bigMoneyAmountFont: TextUnit = 60.sp
    val keypadMoneyAmountFont: TextUnit = 100.sp
}

internal val LocalTypography = staticCompositionLocalOf { AppTypography() }
