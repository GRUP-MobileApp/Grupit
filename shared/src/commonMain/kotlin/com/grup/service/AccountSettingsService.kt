package com.grup.service

import com.grup.interfaces.ISettingsDataStore
import com.grup.other.AccountSettings

internal class AccountSettingsService(
    private val settingsDataStore: ISettingsDataStore
): ViewableAccountSettingsService() {
    fun toggleNotificationType(notification: AccountSettings.Notifications): Boolean {
        settingsDataStore.putBoolean(
            notification.type,
            !isNotificationTypeToggled(notification)
        )
        return isNotificationTypeToggled(notification)
    }
}