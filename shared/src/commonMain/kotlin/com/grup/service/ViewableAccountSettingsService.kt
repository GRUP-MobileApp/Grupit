package com.grup.service

import com.grup.interfaces.ISettingsDataStore
import com.grup.other.AccountSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class ViewableAccountSettingsService : KoinComponent {
    private val settingsDataStore: ISettingsDataStore by inject()
    fun isNotificationTypeToggled(notification: AccountSettings.GroupNotificationType): Boolean {
        return settingsDataStore.getBoolean(
            notification.type
        ) ?: true
    }
}
