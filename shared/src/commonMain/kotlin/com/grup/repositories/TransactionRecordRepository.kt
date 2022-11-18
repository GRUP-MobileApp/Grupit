package com.grup.repositories

import com.grup.interfaces.ITransactionRecordRepository
import com.grup.models.TransactionRecord
import io.realm.kotlin.Configuration
import io.realm.kotlin.RealmConfiguration

internal open class TransactionRecordRepository :
    BaseRealmRepository(), ITransactionRecordRepository {
    override val config: Configuration =
        RealmConfiguration.Builder(schema = setOf(TransactionRecord::class)).build()

    override fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord? {
        return realm.writeBlocking {
            copyToRealm(transactionRecord)
        }
    }

    override fun close() {
        realm.close()
    }
}