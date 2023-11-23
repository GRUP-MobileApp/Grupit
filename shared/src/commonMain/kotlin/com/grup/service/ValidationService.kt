package com.grup.service

import com.grup.exceptions.ValidationException

internal class ValidationService {
    internal companion object {
        val usernameRegex: Regex =
            Regex("^[a-zA-Z0-9._-]+$")

        val nameRegex: Regex =
            Regex("^[a-zA-Z ,.'-]+$")
    }

    fun validateUsername(username: String) {
        if (username.isBlank()) {
            throw ValidationException("Empty username")
        } else if (!username.matches(usernameRegex)) {
            throw ValidationException("Contains non alphanumeric characters")
        } else if (username.length > 12) {
            throw ValidationException("Username can only be at most 12 characters long")
        } else if (username.length < 5) {
            throw ValidationException("Username must be at least 5 characters")
        }
    }

    fun displayName(displayName: String) {
        if (displayName.isBlank()) {
            throw ValidationException("Empty first/last name")
        } else if (!displayName.matches(nameRegex)) {
            throw ValidationException("Invalid characters in display name")
        } else if (displayName.length > 28) {
            throw ValidationException("Max 20 characters for first and last name")
        }
    }
}