package com.grup.models

import com.grup.exceptions.MissingFieldException
import com.grup.other.getCurrentTime
import io.realm.kotlin.types.EmbeddedRealmObject

class TransactionRecord : EmbeddedRealmObject {
    companion object {
        const val PENDING = "PENDING"
        const val REJECTED = "REJECTED"
    }

    var debtorUserInfo: UserInfo? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord missing debtorUserInfo")
    var balanceChange: Double? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord missing balanceChange")
    var dateCreated: String = getCurrentTime()
    var dateAccepted: String = PENDING

    val isAccepted: Boolean
        get() = !(dateAccepted == PENDING || dateAccepted == REJECTED)
}
