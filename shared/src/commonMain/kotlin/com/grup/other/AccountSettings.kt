package com.grup.other

import com.grup.service.ViewableAccountSettingsService

object AccountSettings {
    enum class GroupNotificationType(val type: String) {
        NEW_DEBT_ACTION("NewDebtAction"),
        NEW_GROUP_INVITE("NewGroupInvite"),
        NEW_SETTLE_ACTION("NewSettleAction"),
        ACCEPT_DEBT_ACTION("AcceptDebtAction"),
        ACCEPT_SETTLE_ACTION("AcceptSettleAction"),
    }
}

object NotificationPermissions : ViewableAccountSettingsService() {
    fun isNotificationTypeToggled(notificationName: String): Boolean {
        return AccountSettings.GroupNotificationType.entries.find {
            it.type == notificationName
        }?.let { notification ->
            NotificationPermissions.isNotificationTypeToggled(notification)
        } == true
    }
}
