package com.grup.interfaces

import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.Flow

internal interface IDebtActionRepository : IRepository {
    fun createDebtAction(
        transaction: DatabaseWriteTransaction,
        debtee: UserInfo,
        transactionRecords: List<TransactionRecord>,
        message: String,
        platform: DebtAction.Platform
    ): DebtAction?

    fun updateDebtAction(
        transaction: DatabaseWriteTransaction,
        debtAction: DebtAction,
        block: DebtAction.() -> Unit
    ): DebtAction?

    fun findAllDebtActionsAsFlow(): Flow<List<DebtAction>>

    fun deleteDebtAction(
        transaction: DatabaseWriteTransaction,
        debtAction: DebtAction
    ): DebtAction?
}
