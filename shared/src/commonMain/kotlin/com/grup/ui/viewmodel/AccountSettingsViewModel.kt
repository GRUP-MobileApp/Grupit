package com.grup.ui.viewmodel

import com.grup.models.User
import com.grup.other.AccountSettings
import com.grup.platform.signin.AuthManager
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


internal class AccountSettingsViewModel : LoggedInViewModel(), KoinComponent {
    enum class Pages(pageNumber: Int) {
        MAIN_SETTINGS_PAGE(0),
        EDIT_PROFILE_PAGE(1),
        EDIT_DISPLAY_NAME_PAGE(2)
    }

    companion object {
        val groupNotificationEntries: Map<String, Array<AccountSettings.GroupNotificationType>> = mapOf(
            "Incoming money requests" to arrayOf(
                AccountSettings.GroupNotificationType.NEW_DEBT_ACTION,
                AccountSettings.GroupNotificationType.NEW_SETTLE_ACTION
            ),
            "Updates to your outgoing requests" to arrayOf(
                AccountSettings.GroupNotificationType.ACCEPT_DEBT_ACTION,
                AccountSettings.GroupNotificationType.ACCEPT_SETTLE_ACTION
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
    ): Boolean = notificationTypes.map { notificationType ->
        apiServer.toggleGroupNotificationType(notificationType)
    }.reduce { acc, isToggled ->
        acc && isToggled
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun logOut(onSuccess: () -> Unit) = GlobalScope.launch {
        selectedGroup = null
        authManager.getSignInManagerFromProvider(apiServer.authProvider)?.signOut()
        apiServer.logOut()
        onSuccess()
    }
}