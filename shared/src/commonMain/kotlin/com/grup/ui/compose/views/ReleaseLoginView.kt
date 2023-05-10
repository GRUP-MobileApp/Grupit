package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.H1Text
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.viewmodel.LoginViewModel
import com.grup.platform.signin.GoogleSignInManager
import com.grup.ui.compose.GoogleSignInButton

internal class ReleaseLoginView(
    private val googleSignInManager: GoogleSignInManager? = null
) : Screen {
    @Composable
    override fun Content() {
        val loginViewModel = LoginViewModel()
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            ReleaseLoginLayout(
                loginViewModel = loginViewModel,
                navigator = navigator,
                googleSignInManager = googleSignInManager
            )
        }
    }
}

@Composable
private fun ReleaseLoginLayout(
    loginViewModel: LoginViewModel,
    navigator: Navigator,
    googleSignInManager: GoogleSignInManager?
) {
    val loginResult:
            LoginViewModel.LoginResult by loginViewModel.loginResult.collectAsStateWithLifecycle()

    when (loginResult) {
        is LoginViewModel.LoginResult.SuccessGoogleLogin -> {
            navigator.push(
                MainView(logOutAuthProviderOnClick = { googleSignInManager!!.signOut() })
            )
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
                text = "GRUP",
                fontSize = 70.sp,
                color = AppTheme.colors.onSecondary
            )

            Spacer(modifier = Modifier.height(50.dp))

            googleSignInManager?.let { googleSignInManager ->
                GoogleSignInButton(
                    loginResult = loginResult,
                    googleSignInManager = googleSignInManager,
                    signInCallback = { token ->
                        loginViewModel.loginGoogleAccount(token)
                    }
                )
            }
        }
    }
}
