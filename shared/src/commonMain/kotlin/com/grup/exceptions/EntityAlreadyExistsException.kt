package com.grup.exceptions

class EntityAlreadyExistsException(
    override val message: String? = "Entity already exists"
) : APIException(message)