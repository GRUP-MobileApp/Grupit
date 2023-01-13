package com.grup.models

import com.grup.exceptions.MissingFieldException
import io.realm.kotlin.types.EmbeddedRealmObject

class TransactionRecord : EmbeddedRealmObject {
    object TransactionStatus {
        const val PENDING = "PENDING"
        const val ACCEPTED = "ACCEPTED"
        const val REJECTED = "REJECTED"
    }
    var debtor: String? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord missing debtor")
    var balanceChange: Double? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord missing balanceChange")
    var transactionStatus: String = TransactionStatus.PENDING
}
