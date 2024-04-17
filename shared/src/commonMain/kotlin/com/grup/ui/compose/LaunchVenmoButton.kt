package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.grup.ui.apptheme.AppTheme

@Composable
expect fun LaunchVenmoButton(
    modifier: Modifier = Modifier,
    userAmounts: Map<String, Double>,
    scale: Float = 1f,
    width: Dp = 140.dp,
    height: Dp = 42.dp,
    fontSize: TextUnit = AppTheme.typography.mediumFont,
    enabled: Boolean = true,
    onClick: () -> Unit
)