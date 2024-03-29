package com.grup.exceptions

class DoesNotExistException(
    override val message: String? = "Entity does not exist"
) : APIException(message)