package com.grup.service

import com.grup.models.TransactionRecord
import com.mongodb.client.MongoClient
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.Id
import org.litote.kmongo.getCollection

class TransactionRecordService : KoinComponent {
    private val client: MongoClient by inject()
    private val database = client.getDatabase("transaction_record")
    private val transactionRecordCollection = database.getCollection<TransactionRecord>()

    fun createTransactionRecord(transactionRecord: TransactionRecord): Id<TransactionRecord>? {
        transactionRecordCollection.insertOne(transactionRecord)
        return transactionRecord.id
    }
}