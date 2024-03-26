package com.grup.service

import com.grup.interfaces.ISettingsDataStore
import com.grup.other.AccountSettings
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

open class AccountSettingsService : KoinComponent {
    private val settingsDataStore: ISettingsDataStore by inject()
    fun isNotificationTypeToggled(notification: AccountSettings.GroupNotificationType): Boolean {
        return settingsDataStore.getBoolean(
            notification.type
        ) ?: true
    }

    internal fun getGroupNotificationType(
        notificationType: AccountSettings.GroupNotificationType
    ): Boolean = isNotificationTypeToggled(notificationType)

    internal fun toggleGroupNotificationType(
        notificationType: AccountSettings.GroupNotificationType
    ): Boolean = toggleNotificationType(notificationType)

    private fun toggleNotificationType(notification: AccountSettings.GroupNotificationType): Boolean {
        settingsDataStore.putBoolean(
            notification.type,
            !isNotificationTypeToggled(notification)
        )
        return isNotificationTypeToggled(notification)
    }
}
