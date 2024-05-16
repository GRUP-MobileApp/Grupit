package com.grup.ui.apptheme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.dp

internal data class AppShapes (
    val small: CornerBasedShape = RoundedCornerShape(4.dp),
    val medium: CornerBasedShape = RoundedCornerShape(8.dp),
    val large: CornerBasedShape = RoundedCornerShape(16.dp),
    val extraLarge: CornerBasedShape = RoundedCornerShape(24.dp),

    val topLargeShape: CornerBasedShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
    val bottomLargeShape: CornerBasedShape = RoundedCornerShape(
        bottomStart = 16.dp, bottomEnd = 16.dp
    ),

    val circleShape: RoundedCornerShape = CircleShape
)

internal val LocalShapes = staticCompositionLocalOf { AppShapes() }