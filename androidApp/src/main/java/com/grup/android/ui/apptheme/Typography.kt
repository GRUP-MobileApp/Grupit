package com.grup.android.ui.apptheme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.grup.android.R

private val proxima_nova = FontFamily(
    Font(R.font.proxima_nova, FontWeight.Normal)
)


private val montserrat = FontFamily(
    Font(R.font.montserrat, FontWeight.Normal)
)

data class AppTypography(
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
