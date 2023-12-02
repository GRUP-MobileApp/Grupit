package com.grup.android.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.grup.android.ExceptionHandler
import com.grup.di.initAuthManager
import com.grup.di.initNotificationManager
import com.grup.platform.notification.NotificationManager
import com.grup.platform.signin.AuthManager
import com.grup.ui.compose.Application


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        initAuthManager(authManager = AuthManager())
        initNotificationManager(notificationManager = NotificationManager())

        setContent {
            Application(isDebug = true)
        }
    }
}
