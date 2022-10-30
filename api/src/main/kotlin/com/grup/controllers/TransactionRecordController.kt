package com.grup.controllers

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.models.TransactionRecord
import com.grup.objects.throwIf
import com.grup.service.GroupService
import com.grup.service.TransactionRecordService
import com.grup.service.UserBalanceService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionRecordController : KoinComponent {
    private val transactionRecordService: TransactionRecordService by inject()
    private val groupService: GroupService by inject()
    private val userBalanceService: UserBalanceService by inject()

    fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord {
        transactionRecord.balanceChanges ?: return transactionRecord

        val groupId = transactionRecord.groupId.toString()
        groupService.getByGroupId(groupId)
            ?: throw NotFoundException("Group with id $groupId doesn't exist")

        throwIf(!userBalanceService.getUserIdsByGroupId(groupId).containsAll(
                transactionRecord.balanceChanges.map { balanceChangeRecord ->
                        balanceChangeRecord.userId })) {
            InvalidTransactionRecordException("Group with id $groupId does not contain all Users " +
                    "in transaction with id ${transactionRecord.getId()}")
        }

        transactionRecord.balanceChanges.forEach { balanceChangeRecord ->
            userBalanceService.updateUserBalance(
                groupId,
                balanceChangeRecord.userId,
                balanceChangeRecord.balanceChange
            )
        }
        return transactionRecordService.createTransactionRecord(transactionRecord)
            ?: throw NotCreatedException("Error creating TransactionRecord with id " +
                    transactionRecord.getId())
    }
}