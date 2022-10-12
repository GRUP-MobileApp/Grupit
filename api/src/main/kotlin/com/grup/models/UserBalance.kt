package com.grup.models

import com.grup.serializers.BigDecimalSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id
import java.math.BigDecimal

@Serializable
class UserBalance(
    @Contextual var groupId: Id<Group>,
    @Contextual var userId: Id<User>,
    @Serializable(with = BigDecimalSerializer::class) var balance: BigDecimal
) : BaseEntity() {
    @BsonId
    @Contextual
    val id: Id<UserBalance>? = null

    override fun getId(): String {
        return id.toString()
    }
}
