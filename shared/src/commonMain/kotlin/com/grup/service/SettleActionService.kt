package com.grup.service

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
        return settleActionRepository.createSettleAction(
            SettleAction().apply {
                this.groupId = debtee.groupId
                this.debteeUserInfo = debtee
                this.settleAmount = settleAmount
            }
        ) ?: throw NotCreatedException("Error creating SettleAction for Group with id" +
                    " ${debtee.groupId}")
    }

    fun createSettleActionTransaction(
        settleAction: SettleAction,
        myTransactionRecord: TransactionRecord
    ) {
        settleActionRepository.addSettleActionTransaction(settleAction, myTransactionRecord)
    }

    suspend fun acceptTransactionRecord(settleAction: SettleAction, transactionRecord: TransactionRecord) {
        settleActionRepository.updateSettleAction(settleAction) {
            this.transactionRecords.find {
                it.debtorUserInfo!!.getId() == transactionRecord.debtorUserInfo!!.getId() &&
                        it.dateCreated == transactionRecord.dateCreated
            }?.dateAccepted = getCurrentTime()
        }
    }
    suspend fun rejectTransactionRecord(settleAction: SettleAction, transactionRecord: TransactionRecord) {
        settleActionRepository.updateSettleAction(settleAction) {
            this.transactionRecords.find {
                it.debtorUserInfo!!.getId() == transactionRecord.debtorUserInfo!!.getId() &&
                        it.dateCreated == transactionRecord.dateCreated
            }?.dateAccepted = TransactionRecord.REJECTED
        }
    }

    fun getAllSettleActionsAsFlow() = settleActionRepository.findAllSettleActionsAsFlow()
}
