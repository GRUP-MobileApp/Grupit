package com.grup.exceptions

internal class NegativeBalanceException(
    override val message: String? = "Transaction results in negative balance"
) : APIException(message)