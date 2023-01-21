package com.grup.controllers

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.service.DebtActionService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object DebtActionController : KoinComponent {
    private val debtActionService: DebtActionService by inject()

    fun createDebtAction(transactionRecords: List<TransactionRecord>,
                         debtee: UserInfo): DebtAction {
        if (transactionRecords.isEmpty()) {
            throw InvalidTransactionRecordException("Empty transaction records")
        }
        return debtActionService.createDebtAction(transactionRecords, debtee)
    }

    fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) {
        debtActionService.acceptDebtAction(debtAction, myTransactionRecord)
    }

    fun getAllDebtActionsAsFlow() = debtActionService.getAllDebtActionsAsFlow()
}