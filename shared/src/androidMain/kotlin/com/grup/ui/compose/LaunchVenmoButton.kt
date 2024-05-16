package com.grup.ui.compose

import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
internal actual fun VenmoButton(
    modifier: Modifier,
    venmoUsername: String,
    amount: Double,
    note: String,
    isRequest: Boolean
) {
    val context = LocalContext.current
    val venmoIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse(
            "https://venmo.com/$venmoUsername?txn=${if (isRequest) "charge" else "pay"}" +
                    "&note=$note&amount=$amount"
        )
    )
    val playStoreIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://play.google.com/store/apps/details?id=com.venmo")
    )

    VenmoIcon {
        try {
            context.packageManager.getPackageInfo("com.venmo", 0)
            context.startActivity(venmoIntent)
        } catch (e: NameNotFoundException) {
            context.startActivity(playStoreIntent)
        }
    }
}