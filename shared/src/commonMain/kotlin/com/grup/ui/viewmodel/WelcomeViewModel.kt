package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.platform.image.cropCenterSquareImage
import com.grup.ui.compose.validateName
import com.grup.ui.compose.validateUsername
import dev.icerock.moko.media.Bitmap
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

internal class WelcomeViewModel : LoggedInViewModel() {
    sealed class NameValidity {
        object Valid : NameValidity()
        data class Invalid(val error: String) : NameValidity()
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
        validateUsername(
            username = username,
            onValid = {
                currentJob = coroutineScope.launch {
                    if (!apiServer.validUsername(username)) {
                        _usernameValidity.value = NameValidity.Invalid("Username taken")
                    } else {
                        _usernameValidity.value = NameValidity.Valid
                    }
                }
            },
            onError = { error ->
                _usernameValidity.value = NameValidity.Invalid(error)
            }
        )
    }

    fun checkFirstNameValidity(firstName: String) {
        _firstNameValidity.value = NameValidity.Pending
        validateName(
            name = firstName,
            onValid = {
                _firstNameValidity.value = NameValidity.Valid
            },
            onError = { error ->
                _firstNameValidity.value = NameValidity.Invalid(error)
            }
        )
    }
    fun checkLastNameValidity(lastName: String) {
        _lastNameValidity.value = NameValidity.Pending
        validateName(
            name = lastName,
            onValid = {
                _lastNameValidity.value = NameValidity.Valid
            },
            onError = { error ->
                _lastNameValidity.value = NameValidity.Invalid(error)
            }
        )
    }

    fun registerUserObject(
        username: String,
        displayName: String,
        profilePictureBitmap: Bitmap?
    ) = runBlocking {
        apiServer.registerUser(
            username,
            displayName,
            profilePictureBitmap?.let { cropCenterSquareImage(it) }
        )
    }
}
