package com.grup.device

import com.grup.interfaces.ISettingsDataStore
import com.grup.repositories.SettingsDataStore

object SettingsManager {
    internal val settingsDataStore: ISettingsDataStore = SettingsDataStore()

    object AccountSettings {
        enum class GroupNotificationType {
            NewDebtAction, NewGroupInvite, NewSettleAction, NewSettleActionTransaction,
            AcceptDebtAction, AcceptSettleActionTransaction
        }

        fun getGroupNotificationType(notificationType: String): Boolean =
            settingsDataStore.getBoolean(notificationType) ?: true

        fun toggleGroupNotificationType(notificationType: String): Boolean {
            settingsDataStore.putBoolean(
                notificationType,
                !getGroupNotificationType(notificationType)
            )
            return getGroupNotificationType(notificationType)
        }
    }

    object LoginSettings {
        var userId: String by settingsDataStore::userId
    }

    object InstanceSettings {
        var hasViewedTutorial: Boolean
            get() = settingsDataStore.getBoolean("Tutorial") ?: false
            set(value) { settingsDataStore.putBoolean("Tutorial", value) }
    }
}