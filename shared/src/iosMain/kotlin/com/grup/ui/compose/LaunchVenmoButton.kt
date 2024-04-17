package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

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
    val venmoUrl = NSURL(string = "venmo://paycharge?txn=pay&recipients=vkuan&amount=10&note=Note")

    H1ConfirmTextButton(
        text = "Venmo",
        scale = scale,
        width = width,
        height = height,
        fontSize = fontSize,
        enabled = enabled
    ) {
        with(UIApplication.sharedApplication) {
            openURL(
                if (canOpenURL(venmoUrl)) venmoUrl else NSURL(string = "http://venmo.com/")
            )
        }
        onClick()
    }
}