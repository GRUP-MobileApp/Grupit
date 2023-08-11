package com.grup.interfaces

import com.grup.models.SettleAction
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.Flow

internal interface ISettleActionRepository : IRepository {
    suspend fun createSettleAction(
        settleAmount: Double,
        debtee: UserInfo
    ): SettleAction?

    suspend fun updateSettleAction(
        settleAction: SettleAction,
       block: SettleAction.() -> Unit
    ): SettleAction?

    fun findAllSettleActionsAsFlow(): Flow<List<SettleAction>>
}
