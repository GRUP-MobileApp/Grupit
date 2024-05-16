package com.grup.device

import com.grup.interfaces.ISettingsDataStore
import com.grup.repositories.SettingsDataStore

class SettingsManager {
    private val settingsDataStore: ISettingsDataStore = SettingsDataStore()

    object AccountSettings {
        enum class GroupNotificationType {
            NewDebtAction, NewGroupInvite, NewSettleAction, NewSettleActionTransaction,
            AcceptDebtAction, AcceptSettleActionTransaction
        }
    }

    var userId: String by settingsDataStore::userId

    var hasViewedTutorial: Boolean
        get() = settingsDataStore.getBoolean("Tutorial") ?: false
        set(value) { settingsDataStore.putBoolean("Tutorial", value) }

    fun getGroupNotificationType(notificationType: String): Boolean = settingsDataStore.getBoolean(
        notificationType
    ) ?: true

    fun toggleGroupNotificationType(notificationType: String): Boolean {
        settingsDataStore.putBoolean(
            notificationType,
            !getGroupNotificationType(notificationType)
        )
        return getGroupNotificationType(notificationType)
    }
}