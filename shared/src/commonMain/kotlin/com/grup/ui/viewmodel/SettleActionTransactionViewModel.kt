package com.grup.ui.viewmodel

import com.grup.exceptions.APIException
import com.grup.exceptions.NotFoundException
import com.grup.exceptions.UserNotInGroupException
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

internal class SettleActionTransactionViewModel(private val actionId: String) : LoggedInViewModel() {
    private val _settleActionsFlow: Flow<List<SettleAction>> = apiServer.getAllSettleActionsAsFlow()
    val settleAction: StateFlow<SettleAction> = _settleActionsFlow.map { settleActions ->
        settleActions.find { it.id == actionId } ?: throw NotFoundException("Settle not found")
    }.asState()

    private val _myUserInfosFlow: Flow<List<UserInfo>> = apiServer.getMyUserInfosAsFlow()
    val myUserInfo: StateFlow<UserInfo?> = _myUserInfosFlow.map { userInfos ->
        userInfos.find {
            it.group.id == settleAction.value.userInfo.group.id
        }
    }.asState()

    fun createSettleActionTransaction(
        amount: Double,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.createSettleActionTransaction(
                settleAction.value,
                TransactionRecord.Companion.DataTransactionRecord(
                    myUserInfo.value ?: throw UserNotInGroupException(),
                    amount
                )
            )
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}