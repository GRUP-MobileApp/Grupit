package com.grup.android

import androidx.compose.ui.graphics.vector.ImageVector

data class MenuItem(
    val id: String,
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

data class GroupItem(
    val id: String,
    val index: Int,
    val groupName: String,
    val contentDescription: String,
    val icon: ImageVector
)
