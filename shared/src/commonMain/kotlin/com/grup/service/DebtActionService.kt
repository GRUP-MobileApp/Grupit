package com.grup.service

import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import kotlinx.datetime.Clock
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class DebtActionService : KoinComponent {
    private val debtActionRepository: IDebtActionRepository by inject()

    fun createDebtAction(transactionRecords: List<TransactionRecord>, debtee: UserInfo): DebtAction {
        return debtActionRepository.createDebtAction(DebtAction().apply {
            this.groupId = debtee.groupId
            this.debtee = debtee.userId
            this.debteeName = debtee.nickname
            this.debtTransactions.addAll(transactionRecords)
        }) ?: throw NotCreatedException("Error creating DebtAction for Group with id" +
                " ${debtee.groupId}")
    }

    fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) {
        debtActionRepository.updateDebtAction(debtAction) {
            this.debtTransactions.find { transactionRecord ->
                transactionRecord.debtor == myTransactionRecord.debtor
            }?.dateAccepted = Clock.System.now().toString()
        }
    }

    fun getAllDebtActionsAsFlow() = debtActionRepository.getAllDebtActionsAsFlow()
}