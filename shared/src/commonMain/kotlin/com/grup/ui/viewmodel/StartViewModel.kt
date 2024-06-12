package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import com.grup.APIServer
import com.grup.platform.signin.AuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

class StartViewModel(val isDebug: Boolean = false) : ScreenModel, KoinComponent {
    sealed class SilentSignInResult {
        data class SignedIn(val authProvider: AuthManager.AuthProvider) : SilentSignInResult()
        data class SignedInWelcomeSlideshow(
            val authProvider: AuthManager.AuthProvider
        ) : SilentSignInResult()
        data object NotSignedIn : SilentSignInResult()
        data class Error(val exception: Exception) : SilentSignInResult()
        data object None : SilentSignInResult()
    }

    private val _silentSignInResult = MutableStateFlow<SilentSignInResult>(SilentSignInResult.None)
    val silentSignInResult: StateFlow<SilentSignInResult> = _silentSignInResult

    fun silentSignIn() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
        try {
            if (isDebug) {
                APIServer.debugSilentSignIn()
            } else {
                APIServer.releaseSilentSignIn()
            }?.let { apiServer ->
                LoggedInViewModel.apiServerInstance = apiServer
                val userResult = apiServer.getMyUser(checkDB = true)
                if (userResult != null) {
                    _silentSignInResult.value =
                        SilentSignInResult.SignedIn(apiServer.authProvider)
                } else {
                    _silentSignInResult.value =
                        SilentSignInResult.SignedInWelcomeSlideshow(apiServer.authProvider)
                }
            } ?: run { _silentSignInResult.value = SilentSignInResult.NotSignedIn }
        } catch (e: Exception) {
            _silentSignInResult.value = SilentSignInResult.Error(e)
        }
    }

    fun consumeSilentSignInResult() {
        _silentSignInResult.value = SilentSignInResult.None
    }
}