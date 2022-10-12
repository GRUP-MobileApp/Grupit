package com.grup.objects

import com.grup.models.User
import com.grup.serializers.BigDecimalSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import java.math.BigDecimal

@Serializable
data class BalanceChangeRecord(
    @BsonId
    @Contextual
    val id: Id<BalanceChangeRecord>? = null,
    @Contextual
    val userId: Id<User>,
    @Serializable(with = BigDecimalSerializer::class)
    val balanceChange: BigDecimal
)
