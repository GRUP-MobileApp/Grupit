package com.grup.service

import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.IDebtActionRepository
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.TransactionRecord
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class DebtActionService : KoinComponent {
    private val debtActionRepository: IDebtActionRepository by inject()

    fun createDebtAction(transactionRecords: List<TransactionRecord>, group: Group): DebtAction {
        return debtActionRepository.createDebtAction(DebtAction().apply {
            this.groupId = group.getId()
            this.debtTransactions.addAll(transactionRecords)
        }) ?: throw NotCreatedException("Error creating DebtAction for Group with id" +
                " ${group.getId()}")
    }

    fun getAllDebtActionsAsFlow() = debtActionRepository.getAllDebtActionsAsFlow()
}