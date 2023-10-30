package com.grup.service

import com.grup.exceptions.InvalidTransactionRecordException
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
        debtee: UserInfo,
        transactionRecords: List<TransactionRecord>,
        message: String
    ): DebtAction {
        if (transactionRecords.isEmpty()) {
            throw InvalidTransactionRecordException("Empty transaction records")
        }
        return debtActionRepository.createDebtAction(debtee, transactionRecords, message)
            ?: throw NotCreatedException("Error creating DebtAction for Group with id" +
                " ${debtee.groupId}")
    }

    suspend fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) {
        debtActionRepository.updateDebtAction(debtAction) {
            transactionRecords.find { transactionRecord ->
                transactionRecord.userInfo.id == myTransactionRecord.userInfo.id
            }?.dateAccepted = getCurrentTime()
        }
    }

    suspend fun rejectDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) {
        debtActionRepository.updateDebtAction(debtAction) {
            transactionRecords.find { transactionRecord ->
                transactionRecord.userInfo.id == myTransactionRecord.userInfo.id
            }?.dateAccepted = TransactionRecord.REJECTED
        }
    }

    fun getAllDebtActionsAsFlow() = debtActionRepository.findAllDebtActionsAsFlow()
}
