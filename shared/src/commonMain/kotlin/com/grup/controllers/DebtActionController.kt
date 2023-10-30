package com.grup.controllers

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.service.DebtActionService
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class DebtActionController : KoinComponent {
    private val userInfoService: UserInfoService by inject()
    private val debtActionService: DebtActionService by inject()

    fun createDebtAction(
        transactionRecords: List<TransactionRecord>,
        debtee: UserInfo,
        message: String
    ): DebtAction {
        return debtActionService.createDebtAction(debtee, transactionRecords, message)
    }

    suspend fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) {
        debtActionService.acceptDebtAction(debtAction, myTransactionRecord)
        userInfoService.applyDebtActionTransactionRecord(debtAction, myTransactionRecord)
    }

    suspend fun rejectDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) {
        debtActionService.rejectDebtAction(debtAction, myTransactionRecord)
    }

    fun getAllDebtActionsAsFlow() = debtActionService.getAllDebtActionsAsFlow()
}