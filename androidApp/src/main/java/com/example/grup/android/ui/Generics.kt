package com.example.grup.android.ui

import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit

@Composable
fun h1Text(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    CompositionLocalProvider(
        LocalContentColor provides AppTheme.colors.onPrimary
    ) {
        Text(
            text = text,
            modifier = modifier,
            fontSize = fontSize,
            style = AppTheme.typography.h1
        )
    }
}
