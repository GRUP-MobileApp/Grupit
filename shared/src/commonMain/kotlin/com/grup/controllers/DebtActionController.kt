package com.grup.controllers

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.service.DebtActionService
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DebtActionController : KoinComponent {
    private val userInfoService: UserInfoService by inject()
    private val debtActionService: DebtActionService by inject()

    fun createDebtAction(transactionRecords: List<TransactionRecord>,
                         debtee: UserInfo,
                         message: String): DebtAction {
        if (transactionRecords.isEmpty()) {
            throw InvalidTransactionRecordException("Empty transaction records")
        }
        return debtActionService.createDebtAction(transactionRecords, debtee, message)
    }

    fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) {
        debtActionService.acceptDebtAction(debtAction, myTransactionRecord)
        userInfoService.applyDebtActionTransactionRecord(debtAction, myTransactionRecord)
    }

    fun getAllDebtActionsAsFlow() = debtActionService.getAllDebtActionsAsFlow()
}