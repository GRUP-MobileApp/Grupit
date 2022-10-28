package com.grup.interfaces

import com.grup.models.TransactionRecord

internal interface ITransactionRecordRepository {
    fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord?
}