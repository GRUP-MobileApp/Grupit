package com.grup.exceptions

class NotFoundException(
    override val message: String? = "Entity not found"
) : APIException(message)
