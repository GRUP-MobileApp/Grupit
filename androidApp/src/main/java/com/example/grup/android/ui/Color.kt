package com.example.grup.android.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

val cyan_blue = Color(0xFF0092E3)
val white = Color(0xFFFFFFFF)
val red_error = Color(0xffff0033)
val black = Color(0x00000000)
val grey = Color(0xFF808080)

class AppColors(
    primary: Color,
    secondary: Color,
    onPrimary: Color,
    onSecondary: Color,
    caption: Color,
    error: Color
) {
    var primary by mutableStateOf(primary)
        private set
    var secondary by mutableStateOf(secondary)
        private set
    var onPrimary by mutableStateOf(onPrimary)
        private set
    var onSecondary by mutableStateOf(onSecondary)
        private set
    var caption by mutableStateOf(caption)
        private set
    var error by mutableStateOf(error)
        private set
    fun copy(
        primary: Color = this.primary,
        secondary: Color = this.secondary,
        onPrimary: Color = this.onPrimary,
        onSecondary: Color = this.onSecondary,
        caption: Color = this.caption,
        error: Color = this.error,
    ): AppColors = AppColors(
        primary,
        secondary,
        onPrimary,
        onSecondary,
        caption,
        error,
    )

    fun updateColorsFrom(other: AppColors) {
        primary = other.primary
        secondary = other.secondary
        onPrimary = other.onPrimary
        onSecondary = other.onSecondary
        caption = other.caption
        error = other.error
    }
}

fun appColors(
    primary: Color = cyan_blue,
    secondary: Color = white,
    onPrimary: Color = white,
    onSecondary: Color = black,
    caption: Color = grey,
    error: Color = red_error
): AppColors = AppColors(
    primary = primary,
    secondary = secondary,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    caption = caption,
    error = error
)

internal val LocalColors = staticCompositionLocalOf{ appColors() }
