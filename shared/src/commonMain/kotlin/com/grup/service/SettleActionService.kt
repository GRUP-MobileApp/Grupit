package com.grup.service

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.NegativeBalanceException
import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.ISettleActionRepository
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.other.getCurrentTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SettleActionService : KoinComponent {
    private val settleActionRepository: ISettleActionRepository by inject()

    suspend fun createSettleAction(
        debtor: UserInfo,
        transactionRecords: List<TransactionRecord>
    ): SettleAction {
        if (transactionRecords.isEmpty()) {
            throw InvalidTransactionRecordException("Empty transaction records")
        }
        transactionRecords.sumOf { it.balanceChange }.let { settleAmount ->
            if (debtor.userBalance + settleAmount > 0) {
                throw NegativeBalanceException("SettleAction for $settleAmount results in " +
                        "overpayment")
            }

        }
        return settleActionRepository.createSettleAction(debtor, transactionRecords)
            ?: throw NotCreatedException("Error creating SettleAction for Group with id" +
                    " ${debtor.groupId}")
    }

    suspend fun acceptSettleAction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) {
        settleActionRepository.updateSettleAction(settleAction) {
            this.transactionRecords.find {
                it.userInfo.id == transactionRecord.userInfo.id
            }?.dateAccepted = getCurrentTime()
        }
    }
    suspend fun rejectSettleAction(settleAction: SettleAction, transactionRecord: TransactionRecord) {
        settleActionRepository.updateSettleAction(settleAction) {
            this.transactionRecords.find {
                it.userInfo.id == transactionRecord.userInfo.id
            }?.dateAccepted = TransactionRecord.REJECTED
        }
    }

    fun getAllSettleActionsAsFlow() = settleActionRepository.findAllSettleActionsAsFlow()
}
