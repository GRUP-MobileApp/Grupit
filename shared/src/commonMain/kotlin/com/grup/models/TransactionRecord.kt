package com.grup.models

import com.grup.other.Id
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class TransactionRecord : BaseEntity(), RealmObject {
    data class BalanceChangeRecord (
        val userId: Id,
        val balanceChange: Double
    )
    enum class TransactionType {
        DEBT_ACTION,
        SETTLE_ACTION
    }

    @PrimaryKey override var _id: Id = createId()
    val groupId: Id? = null
    val date: String? = null
    val transactionType: TransactionType? = null
    val balanceChanges: List<BalanceChangeRecord> = emptyList()
}
