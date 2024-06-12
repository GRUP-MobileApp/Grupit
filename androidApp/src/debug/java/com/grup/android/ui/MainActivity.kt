package com.grup.android.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.grup.device.DeviceManager
import com.grup.di.initDeviceManager
import com.grup.platform.notification.NotificationManager
import com.grup.platform.signin.AuthManager
import com.grup.ui.compose.Application


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
//        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        initDeviceManager(
            DeviceManager(
                authManager = AuthManager(),
                notificationManager = NotificationManager()
            )
        )

        setContent {
            Application(isDebug = true)
        }
    }
}
