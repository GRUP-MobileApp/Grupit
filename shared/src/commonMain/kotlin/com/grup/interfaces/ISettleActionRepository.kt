package com.grup.interfaces

import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import kotlinx.coroutines.flow.Flow

internal interface ISettleActionRepository : IRepository {
    fun createSettleAction(settleAction: SettleAction): SettleAction?

    fun updateSettleAction(settleAction: SettleAction,
                           block: SettleAction.() -> Unit): SettleAction?
    fun addSettleActionTransaction(settleAction: SettleAction,
                                   transactionRecord: TransactionRecord): SettleAction?

    fun findAllSettleActionsAsFlow(): Flow<List<SettleAction>>
}
