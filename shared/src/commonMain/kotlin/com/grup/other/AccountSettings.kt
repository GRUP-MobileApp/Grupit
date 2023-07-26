package com.grup.other

import com.grup.service.ViewableAccountSettingsService

object AccountSettings {
    enum class GroupNotificationType(val type: String) {
        NEW_DEBT_REQUEST("NewDebtAction"),
        NEW_GROUP_INVITE("NewGroupInvite"),
        NEW_SETTLE_REQUEST("NewSettleAction"),
        ACCEPT_DEBT_TRANSACTION("AcceptDebtActionTransaction"),
        ACCEPT_SETTLE_TRANSACTION("AcceptSettleActionTransaction"),
        NEW_SETTLE_TRANSACTION("NewSettleActionTransaction")
    }
}

object NotificationPermissions : ViewableAccountSettingsService()