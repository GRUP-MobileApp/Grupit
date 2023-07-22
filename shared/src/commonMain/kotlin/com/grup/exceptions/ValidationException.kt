package com.grup.exceptions

internal class ValidationException(
    override val message: String? = "Validation exception"
) : APIException(message)