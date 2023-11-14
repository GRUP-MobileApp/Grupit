package com.grup.models

import com.grup.models.realm.RealmTransactionRecord
import com.grup.models.realm.RealmUserInfo
import com.grup.other.getCurrentTime

abstract class TransactionRecord {
    companion object {
        const val PENDING = "PENDING"
        const val REJECTED = "REJECTED"

        data class DataTransactionRecord(
            override val userInfo: UserInfo,
            override var balanceChange: Double,
        ) : TransactionRecord() {
            override val dateCreated: String = getCurrentTime()
            override var dateAccepted: String = PENDING
        }
    }

    abstract val userInfo: UserInfo
    abstract var balanceChange: Double
    abstract val dateCreated: String
    abstract var dateAccepted: String

    val isAccepted: Boolean
        get() = !(dateAccepted == PENDING || dateAccepted == REJECTED)

    val isPending: Boolean
        get() = dateAccepted == PENDING

    internal fun toRealmTransactionRecord(): RealmTransactionRecord =
        RealmTransactionRecord().apply {
            _userInfo = this@TransactionRecord.userInfo as RealmUserInfo
            _balanceChange = this@TransactionRecord.balanceChange
            _dateCreated = this@TransactionRecord.dateCreated
            _dateAccepted = this@TransactionRecord.dateAccepted
        }
}
