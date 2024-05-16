package com.grup.ui.viewmodel

import com.grup.exceptions.APIException
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

internal class DebtActionDetailsViewModel(private val actionId: String) : LoggedInViewModel() {
    public override val userObject: User
        get() = super.userObject

    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()

    val debtAction: StateFlow<DebtAction> = _debtActionsFlow.map { debtActions ->
        debtActions.find { it.id == actionId }!!
    }.asState()

    fun acceptDebtAction(
        transactionRecord: TransactionRecord,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.acceptDebtAction(debtAction.value, transactionRecord)
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }

    fun rejectDebtAction(
        transactionRecord: TransactionRecord,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.rejectDebtAction(debtAction.value, transactionRecord)
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}