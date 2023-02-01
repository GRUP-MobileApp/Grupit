package com.grup.service

import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.ISettleActionRepository
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettleActionService : KoinComponent {
    private val settleActionRepository: ISettleActionRepository by inject()

    fun createSettleAction(settleAmount: Double, debtee: UserInfo): SettleAction {
        return settleActionRepository.createSettleAction(SettleAction().apply {
            this.groupId = debtee.groupId
            this.debtee = debtee.userId
            this.debteeName = debtee.nickname
            this.settleAmount = settleAmount
        }) ?: throw NotCreatedException("Error creating SettleAction for Group with id" +
                " ${debtee.groupId}")
    }

    fun addTransactionRecord(settleAction: SettleAction, myTransactionRecord: TransactionRecord) {
        settleActionRepository.updateSettleAction(settleAction) {
            this.debtTransactions.add(myTransactionRecord)
        }
    }

    fun acceptTransactionRecord(settleAction: SettleAction, transactionRecord: TransactionRecord) {
        settleActionRepository.updateSettleAction(settleAction) {
            this.debtTransactions.find {
                it.debtor == transactionRecord.debtor!!
            }?.dateAccepted = Clock.System.now().toString()
        }
    }

    fun getAllSettleActionsAsFlow() = settleActionRepository.findAllSettleActionsAsFlow()
}