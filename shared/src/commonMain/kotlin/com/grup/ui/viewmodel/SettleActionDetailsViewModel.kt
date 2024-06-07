package com.grup.ui.viewmodel

import com.grup.exceptions.APIException
import com.grup.exceptions.UserNotInGroupException
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

internal class SettleActionDetailsViewModel(private val actionId: String) : LoggedInViewModel() {
    public override val userObject: User
        get() = super.userObject

    private val _settleActionsFlow = apiServer.getAllSettleActionsAsFlow()

    val settleAction: StateFlow<SettleAction> = _settleActionsFlow.map { settleActions ->
        settleActions.find { it.id == actionId }!!
    }.asState()

    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    val myUserInfo: StateFlow<UserInfo?> = _myUserInfosFlow.map { userInfos ->
        userInfos.find {
            it.group.id == settleAction.value.userInfo.group.id
        }
    }.asState()

    fun acceptSettleActionTransactionRecord(
        transactionRecord: TransactionRecord,
        onSuccess: (SettleAction) -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.acceptSettleActionTransaction(settleAction.value, transactionRecord)
                ?.let(onSuccess)
        } catch (e: APIException) {
            onError(e.message)
        }
    }

    fun rejectSettleActionTransactionRecord(
        transactionRecord: TransactionRecord,
        onSuccess: (SettleAction) -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.rejectSettleActionTransaction(settleAction.value, transactionRecord)?.let(onSuccess)
        } catch (e: APIException) {
            onError(e.message)
        }
    }

    fun cancelSettleAction(
        onSuccess: (SettleAction) -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.cancelSettleAction(settleAction.value)?.let(onSuccess)
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}