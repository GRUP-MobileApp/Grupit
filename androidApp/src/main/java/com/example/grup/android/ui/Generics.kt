package com.example.grup.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit

@Composable
fun h1Text(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = fontSize,
        style = AppTheme.typography.h1,
        color = AppTheme.colors.textPrimary,
    )
}
