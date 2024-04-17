package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.exceptions.APIException
import com.grup.models.DebtAction
import com.grup.models.User
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class DebtActionDetailsViewModel(private val debtActionId: String) : LoggedInViewModel() {
    public override val userObject: User
        get() = super.userObject

    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
    val debtAction: StateFlow<DebtAction> = _debtActionsFlow.map { debtActions ->
        debtActions.find { it.id == debtActionId }!!
    }.asState()

    fun acceptDebtAction(
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = screenModelScope.launch {
        try {
            with(debtAction.value) {
                transactionRecords.find {
                    it.userInfo.user.id == userObject.id
                }?.let { transactionRecord ->
                    apiServer.acceptDebtAction(this, transactionRecord)
                } ?: throw object : APIException("") { } // TODO: Create exception
                onSuccess()
            }
        } catch (e: APIException) {
            println(e.message)
        }
    }
}