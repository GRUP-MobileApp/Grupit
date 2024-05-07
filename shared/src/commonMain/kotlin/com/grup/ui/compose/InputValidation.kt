package com.grup.ui.compose

import com.grup.service.ValidationService.Companion.nameRegex
import com.grup.service.ValidationService.Companion.usernameRegex

fun validateUsername(username: String, onValid: () -> Unit, onError: (String) -> Unit) {
    if (username.isBlank()) {
        onError("Username cannot be blank")
    } else if (!username.matches(usernameRegex)) {
        onError("Only alphanumeric characters, \' . \', \' - \', and \' _ \' are allowed")
    } else if (username.length > 12) {
        onError("Max 12 characters")
    } else if (username.length < 5) {
        onError("Username must be at least 5 characters")
    } else {
        onValid()
    }
}

fun validateName(
    name: String,
    isBlank: Boolean = false,
    onValid: () -> Unit,
    onError: (String) -> Unit
) {
    if (name.isBlank() && !isBlank) {
        onError("Name cannot be blank")
    } else if (!name.matches(nameRegex)) {
        onError("Contains invalid characters")
    } else if (name.length > 12) {
        onError("Max 12 characters")
    } else {
        onValid()
    }
}

fun validateVenmoUsername(
    venmoUsername: String,
    isBlank: Boolean = false,
    onValid: () -> Unit,
    onError: (String) -> Unit
) {
    if (venmoUsername.isBlank() && !isBlank) {
        onError("Name cannot be blank")
    } else if (!venmoUsername.matches(usernameRegex)) {
        onError("Contains invalid characters")
    } else if (venmoUsername.length > 30) {
        onError("Max 30 characters")
    } else {
        onValid()
    }
}
