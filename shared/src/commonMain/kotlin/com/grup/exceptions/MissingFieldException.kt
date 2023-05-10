package com.grup.exceptions

internal class MissingFieldException(override val message: String? = "Missing field") : APIException(message)