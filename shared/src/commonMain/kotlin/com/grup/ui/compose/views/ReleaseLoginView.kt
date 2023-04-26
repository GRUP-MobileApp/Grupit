package com.grup.ui.compose.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grup.other.collectAsStateWithLifecycle
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.LoadingSpinner
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.viewmodel.LoginViewModel
import com.grup.library.MR
import dev.icerock.moko.resources.compose.painterResource
import kotlin.math.log

@Composable
fun ReleaseLoginView(
    loginViewModel: LoginViewModel,
    googleLoginOnClick: (() -> Unit)? = null,
    loginOnClick: () -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides AppTheme.colors.onSecondary
    ) {
        ReleaseLoginPage(
            loginViewModel = loginViewModel,
            googleLoginOnClick = googleLoginOnClick,
            loginOnClick = loginOnClick
        )
    }
}

@Composable
private fun ReleaseLoginPage(
    loginViewModel: LoginViewModel,
    googleLoginOnClick: (() -> Unit)? = null,
    loginOnClick: () -> Unit
) {
    val loginResult:
            LoginViewModel.LoginResult by loginViewModel.loginResult.collectAsStateWithLifecycle()

    if (loginResult is LoginViewModel.LoginResult.Success) {
        loginOnClick()
    }

    val pendingLogin: Boolean =
        loginResult is LoginViewModel.LoginResult.PendingGoogleLogin

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.primary)
            .padding(AppTheme.dimensions.appPadding)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            H1Text(
                text = "GRUP",
                fontSize = 70.sp,
                color = AppTheme.colors.onSecondary
            )

            Spacer(modifier = Modifier.height(50.dp))

            googleLoginOnClick?.let { googleLoginOnClick ->
                Button(
                    onClick = {
                        if (!pendingLogin) {
                            googleLoginOnClick()
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
                    if (loginResult is LoginViewModel.LoginResult.PendingGoogleLogin) {
                        LoadingSpinner()
                    } else {
                        Image(
                            painter = painterResource(MR.images.ic_logo_google),
                            contentDescription = ""
                        )
                        H1Text(
                            text = "Sign in with Google",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        }
    }
}
