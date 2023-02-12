package com.grup.service

import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.IDebtActionRepository
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.other.getCurrentTime
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class DebtActionService : KoinComponent {
    private val debtActionRepository: IDebtActionRepository by inject()

    fun createDebtAction(
        transactionRecords: List<TransactionRecord>,
        debtee: UserInfo,
        message: String
    ): DebtAction {
        return debtActionRepository.createDebtAction(DebtAction().apply {
            this.debteeUserInfo = debtee
            this.groupId = debtee.groupId
            this.transactionRecords.addAll(transactionRecords)
            this.message = message
        }) ?: throw NotCreatedException("Error creating DebtAction for Group with id" +
                " ${debtee.groupId}")
    }

    fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) {
        debtActionRepository.updateDebtAction(debtAction) {
            this.transactionRecords.find { transactionRecord ->
                transactionRecord.debtorUserInfo!!.getId() ==
                        myTransactionRecord.debtorUserInfo!!.getId()
            }?.dateAccepted = getCurrentTime()
        }
    }

    fun getAllDebtActionsAsFlow() = debtActionRepository.findAllDebtActionsAsFlow()
}
