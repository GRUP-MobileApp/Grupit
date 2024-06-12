package com.grup.ui.viewmodel

import com.grup.exceptions.APIException
import com.grup.exceptions.NotFoundException
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map

internal class DebtActionDetailsViewModel(private val actionId: String) : LoggedInViewModel() {
    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()

    val debtAction: StateFlow<DebtAction> = _debtActionsFlow.map { debtActions ->
        debtActions.find { it.id == actionId } ?: throw NotFoundException()
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