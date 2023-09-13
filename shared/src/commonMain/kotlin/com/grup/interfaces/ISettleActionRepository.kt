package com.grup.interfaces

import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.Flow

internal interface ISettleActionRepository : IRepository {
    suspend fun createSettleAction(
        settleAmount: Double,
        debtee: UserInfo
    ): SettleAction?

    // TODO: Can't update transactionRecords RealmList inside SettleAction.() -> Unit block
    suspend fun updateSettleAction(
        settleAction: SettleAction,
        block: SettleAction.() -> Unit
    ): SettleAction?

    // Using addTransactionRecord in place of updateSettleAction
    suspend fun addTransactionRecord(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ): SettleAction?

    fun findAllSettleActionsAsFlow(): Flow<List<SettleAction>>
}
