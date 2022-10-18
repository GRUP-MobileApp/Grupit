package com.grup.models

import com.grup.objects.BalanceChangeRecord
import com.grup.objects.Id
import com.grup.objects.TransactionType
import com.grup.serializers.DateSerializer
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class TransactionRecord : BaseEntity(), RealmObject {
    val groupId: Id? = null
    @Serializable(with = DateSerializer::class) val date: Date? = null
    val transactionType: TransactionType? = null
    val balanceChanges: List<BalanceChangeRecord>? = null
}

