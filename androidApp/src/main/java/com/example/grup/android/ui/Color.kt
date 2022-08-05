package com.example.grup.android.ui

import androidx.compose.material.Colors
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

val cyan_blue = Color(0xFF0092E3)
val white = Color(0xFFFFFFFF)
val red_error = Color(0xffff0033)

class AppColors(
    primary: Color,
    secondary: Color,
    onPrimary: Color,
    onSecondary: Color,
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
    var error by mutableStateOf(error)
        private set
    fun copy(
        primary: Color = this.primary,
        secondary: Color = this.secondary,
        onPrimary: Color = this.onPrimary,
        onSecondary: Color = this.onSecondary,
        error: Color = this.error,
    ): AppColors = AppColors(
        primary,
        secondary,
        onPrimary,
        onSecondary,
        error,
    )

    fun updateColorsFrom(other: AppColors) {
        primary = other.primary
        secondary = other.secondary
        onPrimary = other.onPrimary
        onSecondary = other.onSecondary
        error = other.error
    }

    fun Colors.contentColorFor(backgroundColor: Color): Color {
        return when (backgroundColor) {
            primary -> onPrimary
            secondary -> onSecondary
            else -> Color.Unspecified
        }
    }
}

fun appColors(
    primary: Color = cyan_blue,
    secondary: Color = white,
    onPrimary: Color = white,
    onSecondary: Color = cyan_blue,
    error: Color = red_error
): AppColors = AppColors(
    primary = primary,
    secondary = secondary,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    error = error
)

internal val LocalColors = staticCompositionLocalOf{ appColors() }
