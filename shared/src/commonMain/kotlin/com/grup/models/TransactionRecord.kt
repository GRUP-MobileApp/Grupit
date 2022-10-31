package com.grup.models

import com.grup.objects.Id
import com.grup.objects.createId
import com.grup.objects.TransactionType
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class TransactionRecord : BaseEntity(), RealmObject {
    data class BalanceChangeRecord (
        val userId: Id,
        val balanceChange: Double
    )

    @PrimaryKey
    override var _id: Id = createId()
    val groupId: Id? = null
    val date: String? = null
    val transactionType: TransactionType? = null
    val balanceChanges: List<BalanceChangeRecord>? = null
}

