package com.grup.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.grup.library.MR
import com.grup.platform.signin.AuthManager
import com.grup.platform.signin.GoogleSignInManager
import com.grup.ui.viewmodel.LoginViewModel
import dev.icerock.moko.resources.compose.painterResource

@Composable
internal actual fun GoogleSignInButton(
    loginResult: LoginViewModel.LoginResult,
    googleSignInManager: GoogleSignInManager,
    signInCallback: (String) -> Unit
) {
    val pendingLogin: Boolean = loginResult is LoginViewModel.LoginResult.PendingLogin

    LaunchedEffect(key1 = googleSignInManager) {
        googleSignInManager.setSignInCallBack(signInCallback)
    }

    Button(
        onClick = {
            if (loginResult !is LoginViewModel.LoginResult.PendingLogin) {
                googleSignInManager.signIn()
            }
        },
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF4285F4),
            contentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
    ) {
        if (loginResult.isSuccessOrPendingLoginAuthProvider(AuthManager.AuthProvider.Google)) {
            LoadingSpinner()
        } else {
            Image(
                painter = painterResource(MR.images.ic_logo_google),
                contentDescription = "Google Icon"
            )
            H1Text(
                text = "Sign in with Google",
                modifier = Modifier.padding(6.dp)
            )
        }
    }
}