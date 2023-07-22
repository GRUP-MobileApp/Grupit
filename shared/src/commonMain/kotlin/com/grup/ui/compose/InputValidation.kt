package com.grup.ui.compose

internal val usernameRegex: Regex =
    Regex("^(?=[a-zA-Z0-9._]{8,20}\$)(?!.*[_.]{2})[^_.].*[^_.]\$")

internal val nameRegex: Regex =
    Regex("^[a-z ,.'-]+$")

fun validateUsername(username: String, onValid: () -> Unit, onError: (String) -> Unit) {
    if (username.isEmpty()) {
        onError("")
    } else if (!username.matches(usernameRegex)) {
        onError("Only alphanumeric characters, \".\", \"-\", and \"_\" are allowed")
    } else if (username.length > 12) {
        onError("Max 12 characters")
    } else if (username.length < 5) {
        onError("Username must be at least 5 characters")
    } else {
        onValid()
    }
}

fun validateName(name: String, onValid: () -> Unit, onError: (String) -> Unit) {
    if (name.isEmpty()) {
        onError("")
    } else if (name.length > 12) {
        onError("Max 12 characters")
    } else if (!name.matches(nameRegex)) {
        onError("Invalid characters")
    } else {
        onValid()
    }
}
