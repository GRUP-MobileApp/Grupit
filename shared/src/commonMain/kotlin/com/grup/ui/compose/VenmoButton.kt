package com.grup.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.grup.library.MR
import com.grup.ui.apptheme.AppTheme
import dev.icerock.moko.resources.compose.painterResource

@Composable
internal expect fun VenmoButton(
    modifier: Modifier = Modifier,
    venmoUsername: String,
    amount: Double,
    note: String,
    isRequest: Boolean = false
)

@Composable
internal fun VenmoIcon(onClick: () -> Unit) {
    Image(
        painter = painterResource(MR.images.venmo_icon),
        contentDescription = "Venmo",
        modifier = Modifier
            .size(AppTheme.dimensions.smallIconSize)
            .clip(AppTheme.shapes.circleShape)
            .clickable(onClick = onClick)
    )
}