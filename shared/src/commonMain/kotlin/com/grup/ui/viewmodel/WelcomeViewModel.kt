package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.exceptions.APIException
import com.grup.models.User
import com.grup.platform.image.cropCenterSquareImage
import com.grup.ui.compose.validateName
import com.grup.ui.compose.validateUsername
import dev.icerock.moko.media.Bitmap
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal class WelcomeViewModel : LoggedInViewModel() {
    sealed class NameValidity {
        data object Valid : NameValidity()
        data class Invalid(val error: String) : NameValidity()
        data object Pending : NameValidity()
        data object None : NameValidity()
    }

    private var currentJob: Job? = null

    private val _usernameValidity = MutableStateFlow<NameValidity>(NameValidity.None)
    val usernameValidity: StateFlow<NameValidity> = _usernameValidity

    private val _firstNameValidity = MutableStateFlow<NameValidity>(NameValidity.None)
    val firstNameValidity: StateFlow<NameValidity> = _firstNameValidity

    private val _lastNameValidity = MutableStateFlow<NameValidity>(NameValidity.None)
    val lastNameValidity: StateFlow<NameValidity> = _lastNameValidity

    fun checkUsername(username: String) {
        if (username.isEmpty()) {
            _usernameValidity.value = NameValidity.None
            return
        }
        _usernameValidity.value = NameValidity.Pending
        currentJob?.cancel()
        validateUsername(
            username = username,
            onValid = {
                currentJob = screenModelScope.launch {
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
        if (firstName.isEmpty()) {
            _usernameValidity.value = NameValidity.None
            return
        }
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
        if (lastName.isEmpty()) {
            _usernameValidity.value = NameValidity.None
            return
        }
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
        venmoUsername: String?,
        profilePictureBitmap: Bitmap?,
        onSuccess: (User) -> Unit,
        onFailure: (String?) -> Unit
    ) = screenModelScope.launch {
        try {
            apiServer.registerUser(
                username,
                displayName,
                venmoUsername,
                profilePictureBitmap?.let { cropCenterSquareImage(it) } ?: byteArrayOf()
            ).let(onSuccess)
        } catch (e: APIException) {
            onFailure(e.message)
        }
    }
}
