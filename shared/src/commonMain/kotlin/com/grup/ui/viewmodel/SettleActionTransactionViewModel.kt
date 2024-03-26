package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.exceptions.APIException
import com.grup.exceptions.UserNotInGroupException
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class SettleActionTransactionViewModel(
    private val groupId: String,
    private val settleActionId: String
) : LoggedInViewModel() {
    private val _myUserInfosFlow: Flow<List<UserInfo>> = apiServer.getMyUserInfosAsFlow()

    val myUserInfo: StateFlow<UserInfo> = _myUserInfosFlow.map { userInfos ->
        userInfos.find { it.group.id == groupId } ?: throw UserNotInGroupException()
    }.asState()

    private val _settleActionsFlow: Flow<List<SettleAction>> = apiServer.getAllSettleActionsAsFlow()
    val settleAction: StateFlow<SettleAction> = _settleActionsFlow.map { settleActions ->
        settleActions.find { it.id == settleActionId }!!
    }.asState()

    fun createSettleActionTransaction(
        amount: Double,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = screenModelScope.launch {
        try {
            apiServer.createSettleActionTransaction(
                settleAction.value,
                TransactionRecord.Companion.DataTransactionRecord(myUserInfo.value, amount)
            ) ?: throw object : APIException("") { }
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}