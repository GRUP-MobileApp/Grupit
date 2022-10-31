package com.grup.service

import com.grup.interfaces.ITransactionRecordRepository
import com.grup.models.TransactionRecord
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class TransactionRecordService : KoinComponent {
    private val transactionRecordRepository: ITransactionRecordRepository by inject()

    fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord? {
        return transactionRecordRepository.createTransactionRecord(transactionRecord)
    }
}