package com.grup.android.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.braintreepayments.api.BraintreeClient
import com.braintreepayments.api.ClientTokenProvider
import com.braintreepayments.api.VenmoAccountNonce
import com.braintreepayments.api.VenmoClient
import com.braintreepayments.api.VenmoListener
import com.grup.di.initAuthManager
import com.grup.di.initNotificationManager
import com.grup.platform.notification.NotificationManager
import com.grup.platform.signin.AuthManager
import com.grup.ui.compose.Application
import java.lang.Exception


class MainActivity : AppCompatActivity(), VenmoListener {
    private var braintreeClient: BraintreeClient? = null
    private var venmoClient: VenmoClient? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        initAuthManager(authManager = AuthManager())
        initNotificationManager(notificationManager = NotificationManager())

        setContent {
            Application(isDebug = true)
        }
    }

    override fun onVenmoSuccess(p0: VenmoAccountNonce) {
        TODO("Not yet implemented")
    }

    override fun onVenmoFailure(p0: Exception) {
        TODO("Not yet implemented")
    }
}
