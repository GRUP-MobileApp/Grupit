package com.grup.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import platform.Foundation.NSURL
import platform.Foundation.NSURLComponents
import platform.Foundation.NSURLQueryItem
import platform.UIKit.UIApplication

@Composable
internal actual fun VenmoButton(
    modifier: Modifier,
    venmoUsername: String,
    amount: Double,
    note: String,
    isRequest: Boolean
) {
    val venmoURL = NSURLComponents(string = "https://venmo.com/$venmoUsername")
    venmoURL.queryItems = listOf(
        NSURLQueryItem(name = "txn", value = if (isRequest) "charge" else "pay"),
        NSURLQueryItem(name = "note", value = note),
        NSURLQueryItem(name = "amount", value = amount.toString())
    )

    VenmoIcon {
        with(UIApplication.sharedApplication) {
            openURL(
                with(venmoURL.URL) {
                    if (this != null && canOpenURL(this)) this
                    else NSURL(string = "https://apps.apple.com/app/venmo/id351727428")
                }
            )
        }
    }
}