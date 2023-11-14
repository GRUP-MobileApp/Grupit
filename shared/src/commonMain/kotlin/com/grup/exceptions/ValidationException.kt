package com.grup.exceptions

class ValidationException(
    override val message: String? = "Validation exception"
) : APIException(message)