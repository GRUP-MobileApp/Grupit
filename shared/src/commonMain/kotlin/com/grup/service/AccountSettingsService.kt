package com.grup.service

import com.grup.interfaces.ISettingsDataStore
import com.grup.other.AccountSettings
import org.koin.core.component.inject

internal class AccountSettingsService : ViewableAccountSettingsService() {
    private val settingsDataStore: ISettingsDataStore by inject()
    fun toggleNotificationType(notification: AccountSettings.GroupNotificationType): Boolean {
        settingsDataStore.putBoolean(
            notification.type,
            !isNotificationTypeToggled(notification)
        )
        return isNotificationTypeToggled(notification)
    }
}