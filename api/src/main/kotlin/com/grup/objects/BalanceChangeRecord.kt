package com.grup.objects

import com.grup.serializers.BigDecimalSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class BalanceChangeRecord(
    @Contextual
    val userId: Id,
    @Serializable(with = BigDecimalSerializer::class)
    val balanceChange: BigDecimal
)
