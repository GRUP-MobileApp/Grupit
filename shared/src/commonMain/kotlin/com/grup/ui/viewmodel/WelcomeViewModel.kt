package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.platform.image.cropCenterSquareImage
import dev.icerock.moko.media.Bitmap
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class WelcomeViewModel : LoggedInViewModel() {
    sealed class NameValidity {
        object Valid : NameValidity()
        class Invalid(val error: String) : NameValidity()
        object Pending : NameValidity()
        object None : NameValidity()
    }

    private var currentJob: Job? = null

    private val _usernameValidity = MutableStateFlow<NameValidity>(NameValidity.None)
    val usernameValidity: StateFlow<NameValidity> = _usernameValidity

    private val _firstNameValidity = MutableStateFlow<NameValidity>(NameValidity.None)
    val firstNameValidity: StateFlow<NameValidity> = _firstNameValidity

    private val _lastNameValidity = MutableStateFlow<NameValidity>(NameValidity.None)
    val lastNameValidity: StateFlow<NameValidity> = _lastNameValidity

    fun checkUsername(username: String) {
        _usernameValidity.value = NameValidity.Pending
        currentJob?.cancel()
        if (username.isEmpty()) {
            _usernameValidity.value = NameValidity.None
        } else if (!username.matches(Regex("^[a-zA-Z/d_.-]*$"))) {
            _usernameValidity.value =
                NameValidity.Invalid(
                    "Only alphanumeric characters, \".\", \"-\", and \"_\" are " +
                            "allowed"
                )
        } else if (username.length < 5) {
            _usernameValidity.value =
                NameValidity.Invalid("Username must be at least 5 characters")
        } else {
            currentJob = coroutineScope.launch {
                if (!apiServer.validUsername(username)) {
                    _usernameValidity.value = NameValidity.Invalid("Username taken")
                } else {
                    _usernameValidity.value = NameValidity.Valid
                }
            }
        }
    }

    fun checkFirstNameValidity(firstName: String) {
        _firstNameValidity.value = NameValidity.Pending
        if (firstName.isEmpty()) {
            _firstNameValidity.value = NameValidity.None
        } else if (!firstName.matches(Regex("^[a-zA-Z]*$"))) {
            _firstNameValidity.value = NameValidity.Invalid("Alphabetic characters only")
        } else {
            _firstNameValidity.value = NameValidity.Valid
        }
    }
    fun checkLastNameValidity(lastName: String) {
        _lastNameValidity.value = NameValidity.Pending
        if (lastName.isEmpty()) {
            _lastNameValidity.value = NameValidity.None
        } else if (!lastName.matches(Regex("^[a-zA-Z]*$"))) {
            _lastNameValidity.value = NameValidity.Invalid("Alphabetic characters only")
        } else {
            _lastNameValidity.value = NameValidity.Valid
        }
    }

    fun registerUserObject(
        username: String,
        displayName: String,
        profilePictureBitmap: Bitmap?
    ) = runBlocking {
        apiServer.registerUser(
            username,
            displayName,
            profilePictureBitmap?.let { cropCenterSquareImage(it.toByteArray()) }
        )
    }
}
