package com.grup.models

import com.grup.objects.Id
import com.grup.objects.createId
import com.grup.serializers.BigDecimalSerializer
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
class UserBalance : BaseEntity(), RealmObject {
    @PrimaryKey
    override var _id: Id = createId()
    var groupId: Id? = null
    var userId: Id? = null
    @Serializable(with = BigDecimalSerializer::class) var balance: BigDecimal? = null
}
