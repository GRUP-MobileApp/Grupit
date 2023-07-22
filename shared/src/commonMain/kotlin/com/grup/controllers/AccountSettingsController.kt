package com.grup.controllers

import com.grup.other.AccountSettings
import com.grup.service.AccountSettingsService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class AccountSettingsController : KoinComponent {
    private val accountSettingsService: AccountSettingsService by inject()

    fun getGroupNotificationNewSettleRequests(): Boolean =
        accountSettingsService.isNotificationTypeToggled(
            AccountSettings.Notifications.NEW_SETTLE_REQUEST
        )

    fun toggleGroupNotificationNewSettleRequests(): Boolean =
        accountSettingsService.toggleNotificationType(
            AccountSettings.Notifications.NEW_SETTLE_REQUEST
        )
}