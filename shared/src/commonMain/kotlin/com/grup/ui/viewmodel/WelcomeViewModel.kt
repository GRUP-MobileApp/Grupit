package com.grup.ui.viewmodel

import com.grup.exceptions.APIException
import com.grup.exceptions.ValidationException
import com.grup.platform.image.cropCenterSquareImage
import com.grup.service.ValidationService
import dev.icerock.moko.media.Bitmap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

internal class WelcomeViewModel(val name: String? = null) : LoggedInViewModel() {
    private val validationService: ValidationService = ValidationService()

    sealed class NameValidity {
        data object Valid : NameValidity()
        data class Invalid(val error: String) : NameValidity()
        data object Pending : NameValidity()
        data object None : NameValidity()
    }

    private val _usernameValidity = MutableStateFlow<NameValidity>(NameValidity.None)
    val usernameValidity: StateFlow<NameValidity> = _usernameValidity

    private val _displayNameValidity = MutableStateFlow(
        if (!name.isNullOrBlank()) NameValidity.Valid
        else NameValidity.None
    )
    val displayNameValidity: StateFlow<NameValidity> = _displayNameValidity

    private val _venmoUsernameValidity = MutableStateFlow<NameValidity>(NameValidity.None)
    val venmoUsernameValidity: StateFlow<NameValidity> = _venmoUsernameValidity

    fun checkUsername(username: String) {
        if (username.isEmpty()) {
            _usernameValidity.value = NameValidity.None
        } else {
            _usernameValidity.value = NameValidity.Pending
            try {
                validationService.validateUsername(username)
                launchJob(allowCancel = true) {
                    if (apiServer.usernameExists(username)) {
                        _usernameValidity.value = NameValidity.Invalid("Username taken")
                    } else {
                        _usernameValidity.value = NameValidity.Valid
                    }
                }
            } catch (e: ValidationException) {
                _usernameValidity.value = NameValidity.Invalid(e.message ?: "")
            }
        }
    }

    fun checkDisplayNameValidity(displayName: String) {
        if (displayName.isEmpty()) {
            _displayNameValidity.value = NameValidity.None
        } else {
            _displayNameValidity.value = NameValidity.Pending
            try {
                validationService.validateName(displayName)
                _displayNameValidity.value = NameValidity.Valid
            } catch (e: ValidationException) {
                _displayNameValidity.value = NameValidity.Invalid(e.message ?: "")
            }
        }
    }

    fun checkVenmoUsernameValidity(venmoUsername: String) {
        if (venmoUsername.isEmpty()) {
            _venmoUsernameValidity.value = NameValidity.None
        } else {
            _venmoUsernameValidity.value = NameValidity.Pending
            try {
                validationService.validateVenmoUsername(venmoUsername = venmoUsername)
                _venmoUsernameValidity.value = NameValidity.Valid
            } catch (e: ValidationException) {
                _venmoUsernameValidity.value = NameValidity.Invalid(e.message ?: "")
            }
        }
    }

    fun registerUserObject(
        username: String,
        displayName: String,
        venmoUsername: String?,
        profilePictureBitmap: Bitmap?,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.registerUser(
                username,
                displayName,
                venmoUsername,
                profilePictureBitmap?.let { cropCenterSquareImage(it) } ?: byteArrayOf()
            )
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}
