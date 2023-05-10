package com.grup.exceptions

internal class EntityAlreadyExistsException(
    override val message: String? = "Entity already exists"
) : APIException(message)