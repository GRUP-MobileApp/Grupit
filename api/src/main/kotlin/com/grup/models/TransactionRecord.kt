package com.grup.models

import com.grup.objects.BalanceChangeRecord
import com.grup.objects.TransactionType
import com.grup.serializers.DateSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import java.util.Date

@Serializable
class TransactionRecord(
    @Contextual var groupId: Id<Group>,
    @Serializable(with = DateSerializer::class) var date: Date,
    var transactionType: TransactionType,
    var balanceChanges: List<BalanceChangeRecord>
) : BaseEntity() {
    @BsonId
    @Contextual
    val id: Id<TransactionRecord>? = null

    override fun getId(): String {
        return id.toString()
    }
}

