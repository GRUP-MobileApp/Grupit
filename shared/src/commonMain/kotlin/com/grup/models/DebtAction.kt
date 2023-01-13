package com.grup.models

import com.grup.exceptions.MissingFieldException
import com.grup.other.createId
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class DebtAction internal constructor(): BaseEntity(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    var groupId: String? = null
        get() = field
            ?: throw MissingFieldException("DebtAction with id $_id missing groupId")
        internal set
    var date: Instant = Clock.System.now()
    var debtee: String? = null
        get() = field
            ?: throw MissingFieldException("DebtAction with id $_id missing debtee")
        internal set
    var debtTransactions: RealmList<TransactionRecord> = realmListOf()
}