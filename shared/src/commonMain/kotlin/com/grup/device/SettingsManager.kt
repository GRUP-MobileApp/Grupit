package com.grup.device

import com.grup.interfaces.ISettingsDataStore
import com.grup.repositories.SettingsDataStore

object SettingsManager {
    private val settingsDataStore: ISettingsDataStore = SettingsDataStore()

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


        // Apple Sign-In
        var isAppleSignInSuccess: Boolean?
            get() = settingsDataStore.getBoolean("appleSignInStatus")
            set(value) {
                if (value != null) {
                    settingsDataStore.putBoolean("appleSignInStatus", value)
                } else {
                    settingsDataStore.remove("appleSignInStatus")
                }
            }

        var appleToken: String?
            get() = settingsDataStore.getString("appleToken")
            set(value) {
                if (value != null) {
                    settingsDataStore.putString("appleToken", value)
                } else {
                    settingsDataStore.remove("appleToken")
                }
            }
    }

    object InstanceSettings {
        var hasViewedTutorial: Boolean
            get() = settingsDataStore.getBoolean("Tutorial") ?: false
            set(value) { settingsDataStore.putBoolean("Tutorial", value) }
    }
}