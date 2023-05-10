package com.grup.ui.compose

import androidx.compose.runtime.Composable
import com.grup.platform.signin.GoogleSignInManager
import com.grup.ui.viewmodel.LoginViewModel

@Composable
internal actual fun GoogleSignInButton(
    loginResult: LoginViewModel.LoginResult,
    googleSignInManager: GoogleSignInManager,
    signInCallback: (String) -> Unit
) {

}