package com.grup.exceptions

internal class ImageUploadException(
    override val message: String? = "Error uploading image"
) : APIException(message)