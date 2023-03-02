package com.grup.exceptions

class ImageUploadException(
    override val message: String? = "Error uploading image"
) : APIException(message)