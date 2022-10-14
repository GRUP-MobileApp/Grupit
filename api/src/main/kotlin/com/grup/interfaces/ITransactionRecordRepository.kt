package com.grup.interfaces

import com.grup.models.TransactionRecord

interface ITransactionRecordRepository {
    fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord?
}