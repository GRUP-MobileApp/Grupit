package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.TransactionRecord
import com.grup.other.getCurrentTime
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.annotations.PersistedName

@PersistedName("TransactionRecord")
internal class RealmTransactionRecord : TransactionRecord(), EmbeddedRealmObject {
    override val userInfo: RealmUserInfo
        get() = _userInfo
            ?: throw MissingFieldException("TransactionRecord missing debtorUserInfo")
    override var balanceChange: Double
        get() = _balanceChange
            ?: throw MissingFieldException("TransactionRecord missing balanceChange")
        set(value) { _balanceChange = value }
    override val dateCreated: String
        get() = _dateCreated
    override var dateAccepted: String
        get() = _dateAccepted
        set(value) { _dateAccepted = value }

    @PersistedName("userInfo")
    var _userInfo: RealmUserInfo? = null
    @PersistedName("balanceChange")
    var _balanceChange: Double? = null
    @PersistedName("dateCreated")
    var _dateCreated: String = getCurrentTime()
    @PersistedName("dateAccepted")
    var _dateAccepted: String = PENDING
}
