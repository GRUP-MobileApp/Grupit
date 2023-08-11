package com.grup.models

import com.grup.other.getCurrentTime

abstract class TransactionRecord {
    companion object {
        const val PENDING = "PENDING"
        const val REJECTED = "REJECTED"

        data class DataTransactionRecord(
            override val debtorUserInfo: UserInfo,
            override var balanceChange: Double,
        ) : TransactionRecord() {
            override val dateCreated: String = getCurrentTime()
            override var dateAccepted: String = PENDING
        }
    }

    abstract val debtorUserInfo: UserInfo
    abstract var balanceChange: Double
    abstract val dateCreated: String
    abstract var dateAccepted: String

    val isAccepted: Boolean
        get() = !(dateAccepted == PENDING || dateAccepted == REJECTED)
}
