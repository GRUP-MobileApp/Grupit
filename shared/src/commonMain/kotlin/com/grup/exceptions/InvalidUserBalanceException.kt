package com.grup.exceptions

class InvalidUserBalanceException(
    override val message: String? = "Invalid user balance"
) : APIException(message)