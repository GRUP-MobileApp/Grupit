package com.grup.service

import com.grup.interfaces.ISettingsDataStore
import com.grup.other.AccountSettings
import org.koin.core.component.inject

open class AccountSettingsService : ViewableAccountSettingsService() {
    private val settingsDataStore: ISettingsDataStore by inject()

    internal fun getGroupNotificationType(
        notificationType: AccountSettings.GroupNotificationType
    ): Boolean = isNotificationTypeToggled(notificationType)

    internal fun toggleGroupNotificationType(
        notificationType: AccountSettings.GroupNotificationType
    ): Boolean = toggleNotificationType(notificationType)

    private fun toggleNotificationType(notification: AccountSettings.GroupNotificationType): Boolean {
        settingsDataStore.putBoolean(
            notification.name,
            !isNotificationTypeToggled(notification)
        )
        return isNotificationTypeToggled(notification)
    }
}
