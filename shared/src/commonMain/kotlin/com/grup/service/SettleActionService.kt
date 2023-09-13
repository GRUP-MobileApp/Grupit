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

    suspend fun createSettleAction(settleAmount: Double, debtee: UserInfo): SettleAction {
        if (debtee.userBalance < settleAmount) {
            throw NegativeBalanceException("SettleAction for $settleAmount results in negative " +
                    "balance")
        }
        return settleActionRepository.createSettleAction(settleAmount, debtee)
            ?: throw NotCreatedException("Error creating SettleAction for Group with id" +
                    " ${debtee.groupId}")
    }

    suspend fun createSettleActionTransaction(
        settleAction: SettleAction,
        myTransactionRecord: TransactionRecord
    ) {
        if (settleAction.remainingAmount < myTransactionRecord.balanceChange) {
            throw InvalidTransactionRecordException("Can't settle for more than remaining amount")
        }
        settleActionRepository.addTransactionRecord(settleAction, myTransactionRecord)
    }

    suspend fun acceptTransactionRecord(settleAction: SettleAction, transactionRecord: TransactionRecord) {
        settleActionRepository.updateSettleAction(settleAction) {
            this.transactionRecords.find {
                it.debtorUserInfo.id == transactionRecord.debtorUserInfo.id &&
                        it.dateCreated == transactionRecord.dateCreated
            }?.dateAccepted = getCurrentTime()
        }
    }
    suspend fun rejectTransactionRecord(settleAction: SettleAction, transactionRecord: TransactionRecord) {
        settleActionRepository.updateSettleAction(settleAction) {
            this.transactionRecords.find {
                it.debtorUserInfo.id == transactionRecord.debtorUserInfo.id &&
                        it.dateCreated == transactionRecord.dateCreated
            }?.dateAccepted = TransactionRecord.REJECTED
        }
    }

    fun getAllSettleActionsAsFlow() = settleActionRepository.findAllSettleActionsAsFlow()
}
