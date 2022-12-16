package com.grup.android

import android.graphics.drawable.Icon
import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector
)

data class GroupItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector
)
