package com.example.grup.android.ui

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.grup.android.R

private val montserrat = FontFamily(
    Font(R.font.montserrat, FontWeight.Normal)
)

data class AppTypography(
    val h1: TextStyle = TextStyle(
        fontFamily = montserrat,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 24.sp
    ),
    val button: TextStyle = TextStyle(
        fontFamily = montserrat,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
)

internal val LocalTypography = staticCompositionLocalOf { AppTypography() }
