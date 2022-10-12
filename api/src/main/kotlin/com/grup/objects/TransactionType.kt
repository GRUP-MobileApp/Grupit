package com.grup.objects

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    DEBT_ACTION,
    SETTLE_ACTION
}