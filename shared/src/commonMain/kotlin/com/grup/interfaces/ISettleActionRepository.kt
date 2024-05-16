package com.grup.interfaces

import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import com.grup.models.SettleAction
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.Flow

internal interface ISettleActionRepository : IRepository {
    fun createSettleAction(
        transaction: DatabaseWriteTransaction,
        debtee: UserInfo,
        settleActionAmount: Double
    ): SettleAction?

    fun updateSettleAction(
        transaction: DatabaseWriteTransaction,
        settleAction: SettleAction,
        block: SettleAction.() -> Unit
    ): SettleAction?

    fun findAllSettleActionsAsFlow(): Flow<List<SettleAction>>

    fun deleteSettleAction(
        transaction: DatabaseWriteTransaction,
        settleAction: SettleAction
    ): SettleAction?
}
