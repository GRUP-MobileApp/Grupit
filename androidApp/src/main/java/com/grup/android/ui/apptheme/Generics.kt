package com.grup.android.ui.apptheme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun h1Text(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = text,
        modifier = modifier,
        style = AppTheme.typography.h1,
        fontSize = fontSize,
    )
}


@Composable
fun caption(
    text: String,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = TextUnit.Unspecified
) {
    Text(
        text = text,
        modifier = modifier,
        style = AppTheme.typography.smallFont,
        fontSize = fontSize
    )
}

@Composable
fun smallIcon(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        tint = AppTheme.colors.onPrimary,
        modifier = modifier.size(AppTheme.dimensions.iconSize)
    )
}

@Composable
fun smallIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    IconButton(onClick = {  },
        modifier = Modifier
            .size(AppTheme.dimensions.borderIconSize)
            .shadow(
                elevation = AppTheme.dimensions.shadowElevationSize,
                shape = AppTheme.shapes.CircleShape,
                clip = false
            )
            .clip(AppTheme.shapes.CircleShape)
            .background(color = AppTheme.colors.caption)
            .border(
                border = BorderStroke(1.dp, AppTheme.colors.secondary),
                shape = AppTheme.shapes.CircleShape
            )
    ) {
        smallIcon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = modifier.clip(AppTheme.shapes.CircleShape)
        )
    }
}
