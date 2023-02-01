package com.grup.interfaces

import com.grup.models.SettleAction
import kotlinx.coroutines.flow.Flow

internal interface ISettleActionRepository : IRepository {
    fun createSettleAction(settleAction: SettleAction): SettleAction?

    fun updateSettleAction(settleAction: SettleAction,
                           block: SettleAction.() -> Unit): SettleAction?

    fun findAllSettleActionsAsFlow(): Flow<List<SettleAction>>
}
