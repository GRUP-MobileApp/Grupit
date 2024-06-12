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
            throw ValidationException("Username cannot be blank")
        } else if (!username.matches(usernameRegex)) {
            throw ValidationException(
                "Only alphanumeric characters, \' . \', \' - \', and \' _ \' are allowed"
            )
        } else if (username.length > 12) {
            throw ValidationException("Max 12 characters")
        } else if (username.length < 5) {
            throw ValidationException("Username must be at least 5 characters")
        }
    }

    fun validateName(name: String) {
        if (name.isBlank()) {
            throw ValidationException("Name cannot be blank")
        } else if (!name.matches(nameRegex)) {
            throw ValidationException("Contains invalid characters")
        } else if (name.length > 35) {
            throw ValidationException("Max 35 characters")
        }
    }

    fun validateVenmoUsername(venmoUsername: String, allowBlank: Boolean = false) {
        if (venmoUsername.isBlank() && !allowBlank) {
            throw ValidationException("Name cannot be blank")
        } else if (!venmoUsername.matches(usernameRegex)) {
            throw ValidationException(
                "Only alphanumeric characters, \' . \', \' - \', and \' _ \' are allowed"
            )
        } else if (venmoUsername.length > 30) {
            throw ValidationException("Max 30 characters")
        }
    }

    fun validateGroupName(groupName: String) {
        if (groupName.isBlank()) {
            throw ValidationException("Group name cannot be blank")
        } else if (groupName.length > 25) {
            throw ValidationException("Max 25 characters")
        }
    }
}