package com.grup.ui.compose

import android.content.Intent
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
actual fun LaunchVenmoButton(
    modifier: Modifier,
    userAmounts: Map<String, Double>,
    scale: Float,
    width: Dp,
    height: Dp,
    fontSize: TextUnit,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val intent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(
            "venmo://paycharge?txn=pay&recipients=vkuan&amount=10&note=Note"
        )
    )
    H1ConfirmTextButton(
        text = "Venmo",
        scale = scale,
        width = width,
        height = height,
        fontSize = fontSize,
        enabled = enabled
    ) {
        if (intent.resolveActivity(context.packageManager) != null) {
            context.startActivity(intent)
        } else {
            // TODO: Navigate to app store Venmo
        }
        onClick()
    }
}