package com.grup.models

import com.grup.exceptions.MissingFieldException
import io.realm.kotlin.types.EmbeddedRealmObject

class TransactionRecord : EmbeddedRealmObject {
    companion object {
        const val PENDING = "PENDING"
    }

    var debtor: String? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord missing debtor")
    var debtorName: String? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord missing debtorName")
    var balanceChange: Double? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord missing balanceChange")
    var dateAccepted: String = PENDING
}
