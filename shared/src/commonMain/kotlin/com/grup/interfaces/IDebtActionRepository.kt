package com.grup.interfaces

import com.grup.models.DebtAction
import kotlinx.coroutines.flow.Flow

internal interface IDebtActionRepository : IRepository {
    fun createDebtAction(debtAction: DebtAction): DebtAction?

    fun getAllDebtActionsAsFlow(): Flow<List<DebtAction>>
}
