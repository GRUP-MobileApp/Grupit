package com.grup.models

import com.grup.exceptions.MissingFieldException
import com.grup.exceptions.NegativeBalanceException
import com.grup.other.createId
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Clock

class SettleAction internal constructor() : Action(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    override var date: String = Clock.System.now().toString()
    override var groupId: String? = null
        get() = field
            ?: throw MissingFieldException("SettleAction with id $_id missing groupId")
    override var debteeUserInfo: UserInfo? = null
        get() = field
            ?: throw MissingFieldException("SettleAction with id $_id missing debtee")
    override var debtTransactions: RealmList<TransactionRecord> = realmListOf()

    var settleAmount: Double? = null
        get() = field
            ?: throw MissingFieldException("SettleAction with id $_id missing settleAmount")
    val remainingAmount: Double
        get() = (settleAmount!! - acceptedAmount).also { remainingAmount ->
            if (remainingAmount < 0) {
                throw NegativeBalanceException("SettleAction with id $_id has negative remaining " +
                        "balance")
            }
        }
}
