package com.example.grup.android.ui

import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color

val dark_grey = Color(0xFF1F1F1F)
val grey = Color(0xFF292929)
val light_grey = Color(0xFF3D3D3D)
val white = Color(0xFFFFFFFF)
val off_white = Color(0xFFF5F5F5)
val green = Color(0xFF65B540)
val red = Color(0xFFEF1A1A)
val red_error = Color(0xffff0033)

class AppColors(
    primary: Color,
    secondary: Color,
    onPrimary: Color,
    onSecondary: Color,
    confirm: Color,
    deny: Color,
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
    var confirm by mutableStateOf(confirm)
        private set
    var deny by mutableStateOf(deny)
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
        confirm: Color = this.confirm,
        deny: Color = this.deny,
        caption: Color = this.caption,
        error: Color = this.error,
    ): AppColors = AppColors(
        primary,
        secondary,
        onPrimary,
        onSecondary,
        confirm,
        deny,
        caption,
        error,
    )

    fun updateColorsFrom(other: AppColors) {
        primary = other.primary
        secondary = other.secondary
        onPrimary = other.onPrimary
        onSecondary = other.onSecondary
        confirm = other.confirm
        deny = other.deny
        caption = other.caption
        error = other.error
    }
}

fun appColors(
    primary: Color = dark_grey,
    secondary: Color = grey,
    onPrimary: Color = off_white,
    onSecondary: Color = white,
    confirm: Color = green,
    deny: Color = red,
    caption: Color = light_grey,
    error: Color = red_error
): AppColors = AppColors(
    primary = primary,
    secondary = secondary,
    onPrimary = onPrimary,
    onSecondary = onSecondary,
    confirm = confirm,
    deny = deny,
    caption = caption,
    error = error
)

internal val LocalColors = staticCompositionLocalOf{ appColors() }
