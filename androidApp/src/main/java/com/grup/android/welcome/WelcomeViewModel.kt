package com.grup.android.welcome

import android.graphics.Bitmap
import android.graphics.Picture
import androidx.lifecycle.viewModelScope
import com.grup.APIServer
import com.grup.android.ViewModel
import com.grup.exceptions.login.UserObjectNotFoundException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class WelcomeViewModel : ViewModel() {
    val hasUserObject: Boolean
        get() = try {
            userObject
            true
        } catch (e: UserObjectNotFoundException) {
            false
        }

    sealed class UsernameValidity {
        object Valid : UsernameValidity()
        object Invalid : UsernameValidity()
        object Pending : UsernameValidity()
        object None : UsernameValidity()
    }

    private var currentJob: Job = viewModelScope.launch { }

    private val _usernameValidity = MutableStateFlow<UsernameValidity>(UsernameValidity.None)
    val usernameValidity: StateFlow<UsernameValidity> = _usernameValidity

    fun checkUsername(username: String) {
        currentJob.cancel()
        if (username.isBlank()) {
            _usernameValidity.value = UsernameValidity.None
        } else {
            _usernameValidity.value = UsernameValidity.Pending
            currentJob = viewModelScope.launch {
                if (!APIServer.usernameExists(username)) {
                    _usernameValidity.value = UsernameValidity.Valid
                } else {
                    _usernameValidity.value = UsernameValidity.Invalid
                }
            }
        }
    }

    fun registerUserObject(
        username: String,
        displayName: String,
        profilePicture: ByteArray
    ) = viewModelScope.launch {
        APIServer.registerUser(
            username,
            displayName,
            profilePicture
        )
    }
}
