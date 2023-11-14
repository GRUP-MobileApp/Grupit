package com.grup.exceptions

class MissingFieldException(override val message: String? = "Missing field") : APIException(message)