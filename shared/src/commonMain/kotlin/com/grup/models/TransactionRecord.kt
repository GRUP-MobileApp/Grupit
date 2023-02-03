package com.grup.models

import com.grup.exceptions.MissingFieldException
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.annotations.Ignore
import kotlinx.datetime.Clock

class TransactionRecord : EmbeddedRealmObject {
    companion object {
        const val PENDING = "PENDING"
    }

    var debtorUserInfo: UserInfo? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord missing debtorUserInfo")
    var balanceChange: Double? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord missing balanceChange")
    var dateCreated: String = Clock.System.now().toString()
    var dateAccepted: String = PENDING
}
