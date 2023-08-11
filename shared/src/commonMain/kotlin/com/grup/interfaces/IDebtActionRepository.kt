package com.grup.interfaces

import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.Flow

internal interface IDebtActionRepository : IRepository {
    fun createDebtAction(
        debtee: UserInfo,
        transactionRecords: List<TransactionRecord>,
        message: String
    ): DebtAction?

    suspend fun updateDebtAction(debtAction: DebtAction, block: DebtAction.() -> Unit): DebtAction?

    fun findAllDebtActionsAsFlow(): Flow<List<DebtAction>>
}
