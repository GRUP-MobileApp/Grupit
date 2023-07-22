package com.grup.other

import com.grup.service.ViewableAccountSettingsService

object AccountSettings {
    enum class Notifications(val type: String, val defaultValue: Boolean) {
        NEW_SETTLE_REQUEST("NewSettleRequest", true)
    }
}

object NotificationPermissions : ViewableAccountSettingsService()