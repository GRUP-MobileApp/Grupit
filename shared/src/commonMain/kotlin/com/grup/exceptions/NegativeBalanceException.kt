package com.grup.exceptions

class NegativeBalanceException(
    override val message: String? = "Transaction results in negative balance"
) : APIException(message)