package com.grup.interfaces

import com.grup.models.TransactionRecord

internal interface ITransactionRecordRepository : IRepository {
    fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord?
}