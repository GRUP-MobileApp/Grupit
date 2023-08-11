package com.grup.controllers

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.service.SettleActionService
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SettleActionController : KoinComponent {
    private val userInfoService: UserInfoService by inject()
    private val settleActionService: SettleActionService by inject()

    suspend fun createSettleAction(settleAmount: Double, debtee: UserInfo): SettleAction {
        return settleActionService.createSettleAction(settleAmount, debtee).also { settleAction ->
            userInfoService.applySettleAction(settleAction)
        }
    }

    suspend fun createSettleActionTransaction(
        settleAction: SettleAction,
        myTransactionRecord: TransactionRecord
    ) {
        settleActionService.createSettleActionTransaction(settleAction, myTransactionRecord)
    }

    suspend fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) {
        if (transactionRecord.balanceChange > settleAction.remainingAmount) {
            throw InvalidTransactionRecordException("Transaction exceeds Settle amount")
        }
        userInfoService.applyPartialSettleActionTransactionRecord(settleAction, transactionRecord)
        settleActionService.acceptTransactionRecord(settleAction, transactionRecord)
    }

    suspend fun rejectSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) {
        settleActionService.rejectTransactionRecord(settleAction, transactionRecord)
    }

    fun getAllSettleActionsAsFlow() = settleActionService.getAllSettleActionsAsFlow()
}