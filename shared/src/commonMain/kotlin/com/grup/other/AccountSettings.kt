package com.grup.other

import com.grup.service.ViewableAccountSettingsService

object AccountSettings {
    enum class GroupNotificationType {
        NewDebtAction, NewGroupInvite, NewSettleAction, NewSettleActionTransaction,
        AcceptDebtAction, AcceptSettleActionTransaction
    }
}

object NotificationPermissions : ViewableAccountSettingsService() {
    fun isNotificationTypeToggled(notificationName: String): Boolean =
        NotificationPermissions.isNotificationTypeToggled(
            AccountSettings.GroupNotificationType.valueOf(notificationName)
        )
}
