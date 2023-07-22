package com.grup.ui.viewmodel

import com.grup.models.User
import com.grup.platform.signin.AuthManager
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


internal class AccountSettingsViewModel : LoggedInViewModel(), KoinComponent {
    private val authManager: AuthManager by inject()

    public override val userObject: User
        get() = super.userObject

    fun getGroupNotificationNewSettleRequests(): Boolean =
        apiServer.getGroupNotificationNewSettleRequests()
    fun toggleGroupNotificationNewSettleRequests(): Boolean =
        apiServer.toggleGroupNotificationNewSettleRequests()
}