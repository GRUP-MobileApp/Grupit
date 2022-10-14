package com.grup.models

import com.grup.objects.BalanceChangeRecord
import com.grup.objects.Id
import com.grup.objects.TransactionType
import com.grup.serializers.DateSerializer
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
class TransactionRecord(
    val groupId: Id,
    @Serializable(with = DateSerializer::class) val date: Date,
    val transactionType: TransactionType,
    val balanceChanges: List<BalanceChangeRecord>
) : BaseEntity()

