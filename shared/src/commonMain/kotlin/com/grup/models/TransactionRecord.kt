package com.grup.models

import com.grup.exceptions.MissingFieldException
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class TransactionRecord internal constructor() : BaseEntity(), RealmObject {
    object TransactionStatus {
        const val PENDING = "PENDING"
        const val ACCEPTED = "ACCEPTED"
        const val REJECTED = "REJECTED"
    }
    @PrimaryKey override var _id: String = createId()

    var groupId: String? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord with id $_id missing groupId")
        internal set
    var debtee: String? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord with id $_id missing debtee")
        internal set
    var debtor: String? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord with id $_id missing debtor")
        internal set
    var balanceChange: Double? = null
        get() = field
            ?: throw MissingFieldException("TransactionRecord with id $_id missing balanceChange")
        internal set
    var transactionStatus: String = TransactionStatus.PENDING
}
