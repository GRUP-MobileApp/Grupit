package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.exceptions.APIException
import com.grup.exceptions.UserNotInGroupException
import com.grup.models.Action
import com.grup.models.DebtAction
import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class ActionDetailsViewModel(private val actionId: String) : LoggedInViewModel() {
    public override val userObject: User
        get() = super.userObject

    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
    private val _settleActionsFlow = apiServer.getAllSettleActionsAsFlow()

    val action: StateFlow<Action> = combine(
        _debtActionsFlow,
        _settleActionsFlow
    ) { actions: Array<List<Action>> ->
        actions.flatMap { it }.find { it.id == actionId }!!
    }.asState()

    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    val myUserInfo: StateFlow<UserInfo> = _myUserInfosFlow.map { userInfos ->
        userInfos.find {
            it.group.id == action.value.userInfo.group.id
        } ?: throw UserNotInGroupException()
    }.asState()

    fun acceptDebtAction(
        debtAction: DebtAction,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = screenModelScope.launch {
        try {
            debtAction.transactionRecords.find {
                it.userInfo.user.id == userObject.id
            }?.let { transactionRecord ->
                apiServer.acceptDebtAction(debtAction, transactionRecord)
            } ?: throw object : APIException("") { } // TODO: Create exception
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}