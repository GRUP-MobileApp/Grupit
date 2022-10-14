package com.grup.models

import com.grup.objects.Id
import com.grup.serializers.BigDecimalSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
class UserBalance(
    @Contextual val groupId: Id,
    @Contextual val userId: Id,
    @Serializable(with = BigDecimalSerializer::class) var balance: BigDecimal
) : BaseEntity()
