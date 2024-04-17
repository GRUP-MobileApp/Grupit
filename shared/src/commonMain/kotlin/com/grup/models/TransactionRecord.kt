package com.grup.models

import com.grup.other.getCurrentTime
import kotlinx.datetime.Instant

abstract class TransactionRecord internal constructor() : BaseEntity() {
    override var _id: String = ""

    sealed class Status {
        internal companion object {
            const val PENDING = "Pending"
            const val REJECTED = "Rejected"
            const val ACCEPTED = "Accepted"
        }

        abstract val status: String

        data object Pending : Status() {
            override val status: String
                get() = PENDING
        }
        data object Rejected : Status() {
            override val status: String
                get() = REJECTED
        }

        data class Accepted(val date: Instant = getCurrentTime()) : Status() {
            override val status: String
                get() = ACCEPTED
        }
    }

    companion object {
        data class DataTransactionRecord(
            override val userInfo: UserInfo,
            override var balanceChange: Double,
        ) : TransactionRecord() {
            override val dateCreated: Instant = getCurrentTime()
            override var status: Status = Status.Pending
        }
    }

    abstract val userInfo: UserInfo
    abstract var balanceChange: Double
    abstract val dateCreated: Instant
    abstract var status: Status
}
