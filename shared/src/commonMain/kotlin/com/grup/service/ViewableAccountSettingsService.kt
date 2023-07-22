package com.grup.service

import com.grup.interfaces.ISettingsDataStore
import com.grup.other.AccountSettings
import com.grup.repositories.SettingsDataStore

open class ViewableAccountSettingsService internal constructor(
    private val settingsDataStore: ISettingsDataStore = SettingsDataStore()
) {
    fun isNotificationTypeToggled(notification: AccountSettings.Notifications): Boolean {
        return settingsDataStore.getBoolean(
            notification.type
        ) ?: notification.defaultValue
    }
}