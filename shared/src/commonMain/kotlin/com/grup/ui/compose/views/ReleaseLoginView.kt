package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.platform.signin.AuthManager
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.GoogleSignInButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.viewmodel.LoginViewModel

internal class ReleaseLoginView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val loginViewModel = rememberScreenModel { LoginViewModel() }
        val navigator = LocalNavigator.currentOrThrow

        ReleaseLoginLayout(
            loginViewModel = loginViewModel,
            navigator = navigator
        )
    }
}

@Composable
private fun ReleaseLoginLayout(
    loginViewModel: LoginViewModel,
    navigator: Navigator
) {
    val loginResult:
            LoginViewModel.LoginResult by loginViewModel.loginResult.collectAsStateWithLifecycle()

    when (loginResult) {
        is LoginViewModel.LoginResult.SuccessLogin -> {
            navigator.push(MainView())
        }
        is LoginViewModel.LoginResult.SuccessLoginWelcomeSlideshow -> {
            navigator.push(listOf(MainView(), WelcomeView()))
        }
        else -> {}
    }

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
                text = "Grupit",
                fontSize = 70.sp,
                color = AppTheme.colors.onSecondary
            )

            Spacer(modifier = Modifier.height(50.dp))

            if (loginViewModel.allowAuthProvider(AuthManager.AuthProvider.Google)) {
                GoogleSignInButton(
                    loginResult = loginResult,
                    signIn = { loginViewModel.loginGoogleAccount() }
                )
            }
        }
    }
}
