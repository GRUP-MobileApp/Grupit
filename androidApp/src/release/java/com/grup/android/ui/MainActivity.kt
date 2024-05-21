package com.grup.android.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.grup.android.ExceptionHandler
import com.grup.device.DeviceManager
import com.grup.di.initDeviceManager
import com.grup.platform.notification.NotificationManager
import com.grup.platform.signin.AuthManager
import com.grup.platform.signin.GoogleSignInManager
import com.grup.ui.compose.Application

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        AppUpdateManagerFactory.create(applicationContext).let { appUpdateManager ->
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
                if (
                    appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                    && appUpdateInfo.updatePriority() >= 3
                    && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                ) {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        this,
                        AppUpdateOptions.defaultOptions(AppUpdateType.IMMEDIATE),
                        0
                    )
                }
            }
        }

        initDeviceManager(
            DeviceManager(
                authManager = AuthManager(googleSignInManager = GoogleSignInManager(this)),
                notificationManager = NotificationManager()
            )
        )

        setContent {
            Application()
        }
    }
}
