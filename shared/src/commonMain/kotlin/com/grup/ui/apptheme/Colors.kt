package com.grup.ui.apptheme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

internal val dark_grey = Color(0xFF1F1F1F)
internal val grey = Color(0xFF2B2B2B)
internal val light_grey = Color(0xFF3D3D3D)
internal val white = Color(0xFFFFFFFF)
internal val off_white = Color(0xBBF5F5F4)
internal val red_error = Color(0xFFFF0033)

internal val light_blue = Color(0xFF00C7F2)
internal val light_vermilion = Color(0xFFFF7355)

internal val venmo = Color(0xFF008CFF)

internal class AppColors(
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

internal fun appColors(
    primary: Color = dark_grey,
    secondary: Color = grey,
    onPrimary: Color = off_white,
    onSecondary: Color = white,
    confirm: Color = light_blue,
    deny: Color = light_vermilion,
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

internal val LocalColors = staticCompositionLocalOf { appColors() }
