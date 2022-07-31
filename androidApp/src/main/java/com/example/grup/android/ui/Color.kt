package com.example.grup.android.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val cyan_blue = Color(0xFF0092E3)
val white = Color(0xFFFFFFFF)
val red_error = Color(0xffff0033)

class AppColors(
    background: Color,
    button: Color,
    textPrimary: Color,
    error: Color
) {
    var background by mutableStateOf(background)
        private set
    var button by mutableStateOf(button)
        private set
    var textPrimary by mutableStateOf(textPrimary)
        private set
    var error by mutableStateOf(error)
        private set
    fun copy(
        background: Color = this.background,
        button: Color = this.button,
        textPrimary: Color = this.textPrimary,
        error: Color = this.error,
    ): AppColors = AppColors(
        background,
        button,
        textPrimary,
        error,
    )

    fun updateColorsFrom(other: AppColors) {
        background = other.background
        button = other.button
        textPrimary = other.textPrimary
        error = other.error
    }
}

fun appColors(
    background: Color = cyan_blue,
    button: Color = white,
    textPrimary: Color = white,
    error: Color = red_error
): AppColors = AppColors(
    background = background,
    button = button,
    textPrimary = textPrimary,
    error = error
)

internal val LocalColors = staticCompositionLocalOf{ appColors() }
