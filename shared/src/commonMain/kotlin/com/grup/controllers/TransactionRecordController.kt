package com.grup.controllers

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.NotCreatedException
import com.grup.models.TransactionRecord
import com.grup.objects.throwIf
import com.grup.service.GroupService
import com.grup.service.TransactionRecordService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TransactionRecordController : KoinComponent {
    private val transactionRecordService: TransactionRecordService by inject()
    private val groupService: GroupService by inject()

    fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord {
        throwIf(transactionRecord.balanceChanges.isEmpty()) {
            InvalidTransactionRecordException("Empty transaction record")
        }

        groupService.applyTransactionRecord(transactionRecord)
        return transactionRecordService.createTransactionRecord(transactionRecord)
            ?: throw NotCreatedException("Error creating TransactionRecord with id " +
                    transactionRecord.getId())
    }
}