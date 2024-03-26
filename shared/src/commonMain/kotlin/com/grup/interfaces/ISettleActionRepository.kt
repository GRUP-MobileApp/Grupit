package com.grup.interfaces

import com.grup.models.SettleAction
import com.grup.models.UserInfo
import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import kotlinx.coroutines.flow.Flow

internal interface ISettleActionRepository : IRepository {
    fun createSettleAction(
        transaction: DatabaseWriteTransaction,
        debtee: UserInfo,
        settleActionAmount: Double
    ): SettleAction?

    // TODO: Can't update transactionRecords RealmList inside SettleAction.() -> Unit block
    fun updateSettleAction(
        transaction: DatabaseWriteTransaction,
        settleAction: SettleAction,
        block: SettleAction.() -> Unit
    ): SettleAction?

    fun findAllSettleActionsAsFlow(): Flow<List<SettleAction>>
}
