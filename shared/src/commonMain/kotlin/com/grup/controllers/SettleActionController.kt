package com.grup.controllers

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.service.SettleActionService
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object SettleActionController : KoinComponent {
    private val userInfoService: UserInfoService by inject()
    private val settleActionService: SettleActionService by inject()

    fun createSettleAction(settleAmount: Double, debtee: UserInfo): SettleAction {
        return settleActionService.createSettleAction(settleAmount, debtee).also { settleAction ->
            userInfoService.applySettleAction(settleAction)
        }
    }

    fun createSettleActionTransaction(
        settleAction: SettleAction,
        myTransactionRecord: TransactionRecord
    ) {
        settleActionService.createSettleActionTransaction(settleAction, myTransactionRecord)
    }

    fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) {
        if (transactionRecord.balanceChange!! > settleAction.remainingAmount) {
            throw InvalidTransactionRecordException("Transaction exceeds Settle amount")
        }
        userInfoService.applyPartialSettleActionTransactionRecord(settleAction, transactionRecord)
        settleActionService.acceptTransactionRecord(settleAction, transactionRecord)
    }

    fun getAllSettleActionsAsFlow() = settleActionService.getAllSettleActionsAsFlow()
}