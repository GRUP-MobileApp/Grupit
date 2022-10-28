package com.grup.models

import com.grup.objects.BalanceChangeRecord
import com.grup.objects.Id
import com.grup.objects.createId
import com.grup.objects.TransactionType
import com.grup.serializers.DateSerializer
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class TransactionRecord : BaseEntity(), RealmObject {
    @PrimaryKey
    override var _id: Id = createId()
    val groupId: Id? = null
    @Serializable(with = DateSerializer::class) val date: Date? = null
    val transactionType: TransactionType? = null
    val balanceChanges: List<BalanceChangeRecord>? = null
}

