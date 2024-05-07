package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.ktor.http.encodeURLPath
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

@Composable
internal actual fun VenmoButton(
    modifier: Modifier,
    venmoUsername: String,
    amount: Double,
    note: String,
    isRequest: Boolean
) {
    val venmoUrl = NSURL(
        string = "https://venmo.com/$venmoUsername?txn=${if (isRequest) "charge" else "pay"}" +
                "&note=$note&amount=$amount"
    )

    VenmoIcon {
        with(UIApplication.sharedApplication) {
            openURL(
                if (canOpenURL(NSURL(string = "venmo://app"))) venmoUrl
                else NSURL(string = "https://apps.apple.com/app/venmo/id351727428")
            )
        }
    }
}