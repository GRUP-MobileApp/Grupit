package com.grup.exceptions

class InvalidTransactionRecordException(
    override val message: String? = "Bad TransactionRecord"
) : Exception(message)