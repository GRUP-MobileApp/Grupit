package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.platform.signin.AuthManager
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.LoadingSpinner
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.viewmodel.LoginViewModel
import com.grup.ui.compose.GoogleSignInButton

internal class DebugLoginView(
    private val authManager: AuthManager
) : Screen {
    @Composable
    override fun Content() {
        val loginViewModel = LoginViewModel()
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            DebugLoginLayout(
                loginViewModel = loginViewModel,
                navigator = navigator,
                authManager = authManager
            )
        }
    }
}

@Composable
private fun DebugLoginLayout(
    loginViewModel: LoginViewModel,
    navigator: Navigator,
    authManager: AuthManager
) {
    var email: TextFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var password: TextFieldValue by remember { mutableStateOf(TextFieldValue()) }

    val loginResult:
            LoginViewModel.LoginResult by loginViewModel.loginResult.collectAsStateWithLifecycle()

    val pendingLogin: Boolean = loginResult is LoginViewModel.LoginResult.PendingLogin

    when(loginResult) {
        is LoginViewModel.LoginResult.SuccessLogin -> {
            navigator.push(
                MainView(
                    signInManager = authManager.getSignInManagerFromProvider(
                        (loginResult as LoginViewModel.LoginResult.SuccessLogin).authProvider
                    )
                )
            )
        }
        else -> {}
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.primary)
    ) {
        ClickableText(
            text = AnnotatedString("Forgot Password?"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = { /* TODO */ },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                color = AppTheme.colors.onSecondary
            )
        )

    }
    Column(
        modifier = Modifier
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        H1Text(
            text = "GRUP",
            fontSize = 40.sp,
            color = AppTheme.colors.onSecondary
        )

        Spacer(modifier = Modifier.height(50.dp))

        TextField(
            label = {
                H1Text(
                    text = "Username",
                    color = AppTheme.colors.onSecondary,
                    fontSize = 20.sp
                )
            },
            modifier = Modifier.background(AppTheme.colors.secondary),
            textStyle = TextStyle(color = AppTheme.colors.onSecondary),
            value = email,
            onValueChange = { email = it },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = {
                H1Text(
                    text = "Password",
                    color = AppTheme.colors.onSecondary,
                    fontSize = 20.sp
                )
            },
            modifier = Modifier.background(AppTheme.colors.secondary),
            textStyle = TextStyle(color = AppTheme.colors.onSecondary),
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password = it },
            singleLine = true
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(30.dp)
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            if (loginResult is LoginViewModel.LoginResult.Error) {
                (loginResult as LoginViewModel.LoginResult.Error).exception.message?.let { error ->
                    Text(text = error, color = AppTheme.colors.onSecondary)
                }
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp, 0.dp, 20.dp, 0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 40.dp)
            ) {
                Button(
                    onClick = {
                        if (!pendingLogin) {
                            loginViewModel.registerEmailPassword(email.text, password.text)
                        }
                    },
                    shape = AppTheme.shapes.circleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.colors.secondary
                    ),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    if (
                        loginResult.isSuccessOrPendingLoginAuthProvider(
                            AuthManager.AuthProvider.EmailPasswordRegister
                        )
                    ) {
                        LoadingSpinner()
                    } else {
                        H1Text(
                            text = "Sign Up",
                            color = AppTheme.colors.onSecondary,
                            fontSize = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = {
                        if (!pendingLogin) {
                            loginViewModel.loginEmailPassword(email.text, password.text)
                        }
                    },
                    shape = AppTheme.shapes.circleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.colors.confirm
                    ),
                    modifier = Modifier
                        .width(130.dp)
                        .height(50.dp)
                ) {
                    if (
                        loginResult.isSuccessOrPendingLoginAuthProvider(
                            AuthManager.AuthProvider.EmailPassword
                        )
                    ) {
                        LoadingSpinner()
                    } else {
                        H1Text(
                            text = "Login",
                            color = AppTheme.colors.onSecondary,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }

        authManager.googleSignInManager?.let { googleSignInManager ->
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
