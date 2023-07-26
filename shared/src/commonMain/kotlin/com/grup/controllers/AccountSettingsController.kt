package com.grup.controllers

import com.grup.other.AccountSettings
import com.grup.service.AccountSettingsService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class AccountSettingsController : KoinComponent {
    private val accountSettingsService: AccountSettingsService by inject()

    fun getGroupNotificationType(
        notificationType: AccountSettings.GroupNotificationType
    ): Boolean = accountSettingsService.isNotificationTypeToggled(notificationType)

    fun toggleGroupNotificationType(
        notificationType: AccountSettings.GroupNotificationType
    ): Boolean = accountSettingsService.toggleNotificationType(notificationType)
}