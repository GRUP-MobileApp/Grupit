package com.grup.models

import com.grup.objects.Id
import com.grup.serializers.BigDecimalSerializer
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
class UserBalance : BaseEntity(), RealmObject {
    @Contextual
    var groupId: Id? = null
    @Contextual
    var userId: Id? = null
    @Serializable(with = BigDecimalSerializer::class) var balance: BigDecimal? = null
}
