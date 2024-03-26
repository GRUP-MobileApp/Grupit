package com.grup.ui.viewmodel

import com.grup.exceptions.UserNotInGroupException
import com.grup.models.SettleAction
import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

internal class SettleActionDetailsViewModel(
    val groupId: String,
    val settleActionId: String
) : LoggedInViewModel() {
    public override val userObject: User
        get() = super.userObject

    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    val myUserInfo: StateFlow<UserInfo> = _myUserInfosFlow.map { userInfos ->
        userInfos.find { it.group.id == groupId } ?: throw UserNotInGroupException()
    }.asState()

    private val _settleActionsFlow = apiServer.getAllSettleActionsAsFlow()
    val settleAction: StateFlow<SettleAction> = _settleActionsFlow.map { settleActions ->
        settleActions.find { it.id == settleActionId }!!
    }.asState()
}