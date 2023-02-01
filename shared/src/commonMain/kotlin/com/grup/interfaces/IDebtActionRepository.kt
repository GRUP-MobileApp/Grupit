package com.grup.interfaces

import com.grup.models.DebtAction
import kotlinx.coroutines.flow.Flow

internal interface IDebtActionRepository : IRepository {
    fun createDebtAction(debtAction: DebtAction): DebtAction?

    fun updateDebtAction(debtAction: DebtAction, block: DebtAction.() -> Unit): DebtAction?

    fun findAllDebtActionsAsFlow(): Flow<List<DebtAction>>
}
