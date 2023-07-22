package com.grup.service

import com.grup.exceptions.ValidationException
import com.grup.ui.compose.nameRegex
import com.grup.ui.compose.usernameRegex

internal class ValidationService {
    fun validateUsername(username: String) {
        if (username.isEmpty()) {
            throw ValidationException("Empty username")
        } else if (!username.matches(usernameRegex)) {
            throw ValidationException("Contains non alphabet characters")
        } else if (username.length > 12) {
            throw ValidationException("Username can only be at most 12 characters long")
        } else if (username.length < 5) {
            throw ValidationException("Username must be at least 5 characters")
        }
    }

    fun validateNickname(fullName: String) {
        if (fullName.count { it == ' '} != 1) {
            throw ValidationException("Illegal word count")
        } else if (fullName.isBlank()) {
            throw ValidationException("Empty first/last name")
        } else if (fullName.length > 21) {
            throw ValidationException("Max 20 characters for first and last name")
        } else if (fullName.matches(nameRegex)) {
            throw ValidationException("Invalid characters")
        }
    }
}