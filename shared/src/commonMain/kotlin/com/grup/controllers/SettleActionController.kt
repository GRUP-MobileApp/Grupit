package com.grup.controllers

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

    suspend fun createSettleAction(
        debtor: UserInfo,
        transactionRecords: List<TransactionRecord>
    ): SettleAction {
        return settleActionService.createSettleAction(debtor, transactionRecords)
    }

    suspend fun acceptSettleAction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) {
        userInfoService.applySettleActionTransactionRecord(settleAction, transactionRecord)
        settleActionService.acceptSettleAction(settleAction, transactionRecord)
    }

    suspend fun rejectSettleAction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) {
        settleActionService.rejectSettleAction(settleAction, transactionRecord)
    }

    fun getAllSettleActionsAsFlow() = settleActionService.getAllSettleActionsAsFlow()
}