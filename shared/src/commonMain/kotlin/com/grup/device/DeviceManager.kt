package com.grup.device

import com.grup.platform.notification.NotificationManager
import com.grup.platform.signin.AuthManager

data class DeviceManager(
    val authManager: AuthManager = AuthManager(),
    val notificationManager: NotificationManager
) {
    companion object {
        val settingsManager: SettingsManager = SettingsManager()
    }
}