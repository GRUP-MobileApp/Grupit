package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.APIServer
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.platform.signin.AuthManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class StartViewModel : ScreenModel {
    sealed class SilentSignInResult {
        data class SignedIn(val authProvider: AuthManager.AuthProvider) : SilentSignInResult()
        data class SignedInWelcomeSlideshow(
            val authProvider: AuthManager.AuthProvider
        ) : SilentSignInResult()
        object NotSignedIn : SilentSignInResult()
        object None : SilentSignInResult()
    }

    private val _silentSignInResult = MutableStateFlow<SilentSignInResult>(SilentSignInResult.None)
    val silentSignInResult: StateFlow<SilentSignInResult> = _silentSignInResult

    fun silentSignIn(isDebug: Boolean = false) = coroutineScope.launch {
        if (isDebug) {
            APIServer.debugSilentSignIn()
        } else {
            APIServer.releaseSilentSignIn()
        } ?.let { apiServer ->
            injectApiServer(apiServer)
            try {
                apiServer.user
                _silentSignInResult.value = SilentSignInResult.SignedIn(apiServer.authProvider)
            } catch (e: UserObjectNotFoundException) {
                _silentSignInResult.value =
                    SilentSignInResult.SignedInWelcomeSlideshow(apiServer.authProvider)
            }
        } ?: kotlin.run {
            _silentSignInResult.value = SilentSignInResult.NotSignedIn
        }
    }

    private fun injectApiServer(apiServer: APIServer) {
        loadKoinModules(
            module {
                single { apiServer }
            }
        )
    }
}