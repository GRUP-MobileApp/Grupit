package com.grup.ui.viewmodel

import com.grup.models.User
import com.grup.other.AccountSettings
import com.grup.platform.signin.AuthManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


internal class AccountSettingsViewModel : LoggedInViewModel(), KoinComponent {
    companion object {
        val groupNotificationEntries: Map<String, Array<AccountSettings.GroupNotificationType>> = mapOf(
            "Incoming money requests" to arrayOf(
                AccountSettings.GroupNotificationType.NEW_DEBT_REQUEST,
                AccountSettings.GroupNotificationType.NEW_SETTLE_TRANSACTION
            ),
            "Updates to your outgoing requests" to arrayOf(
                AccountSettings.GroupNotificationType.ACCEPT_DEBT_TRANSACTION,
                AccountSettings.GroupNotificationType.ACCEPT_SETTLE_TRANSACTION
            ),
            "New group settle requests" to arrayOf(
                AccountSettings.GroupNotificationType.NEW_SETTLE_REQUEST
            ),
            "Group invites" to arrayOf(
                AccountSettings.GroupNotificationType.NEW_GROUP_INVITE
            )
        )
    }

    private val authManager: AuthManager by inject()

    public override val userObject: User
        get() = super.userObject

    fun getGroupNotificationType(
        vararg notificationTypes: AccountSettings.GroupNotificationType
    ): Boolean = notificationTypes.fold(true) { and, notificationType ->
        and && apiServer.getGroupNotificationType(notificationType)
    }
    fun toggleGroupNotificationType(
        vararg notificationTypes: AccountSettings.GroupNotificationType
    ): Boolean = notificationTypes.fold(true) { and, notificationType ->
        and && apiServer.toggleGroupNotificationType(notificationType)
    }
}