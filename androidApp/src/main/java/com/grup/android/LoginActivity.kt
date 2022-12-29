package com.grup.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grup.android.ui.*

import androidx.compose.material.*
import androidx.compose.ui.platform.LocalContext
import com.grup.android.viewmodels.login.LoginResult
import com.grup.android.viewmodels.login.LoginViewModel


class LoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        setContent {
            AppTheme {
                LoginPage(loginViewModel)
            }
        }
    }
}

@Composable
fun LoginPage(
    loginViewModel: LoginViewModel
) {
    var email: TextFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var password: TextFieldValue by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current

    Box(modifier = Modifier
        .fillMaxSize()
        .background(AppTheme.colors.primary)) {
        ClickableText(
            text = AnnotatedString("Sign up here"),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            onClick = { loginViewModel.registerEmailPassword(email.text, password.text) },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                textDecoration = TextDecoration.Underline,
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
        Text(
            text = "Login",
            style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Monospace),
            color = AppTheme.colors.onSecondary
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text(text = "Username", color = AppTheme.colors.onSecondary) },
            textStyle = TextStyle(color = AppTheme.colors.onSecondary),
            value = email,
            onValueChange = { email = it }
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text(text = "Password", color = AppTheme.colors.onSecondary) },
            textStyle = TextStyle(color = AppTheme.colors.onSecondary),
            value = password,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password = it }
        )

        // Error message
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .height(30.dp)
                .padding(5.dp)
                .fillMaxWidth()
        ) {
            val loginResult: LoginResult by loginViewModel.loginResult.collectAsState()
            when(loginResult.status) {
                LoginResult.LoginStatus.SUCCESS -> {
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
                LoginResult.LoginStatus.ERROR ->
                    loginResult.error?.message?.let {
                        Text(text = it, color = AppTheme.colors.onSecondary)
                    }
                null -> {}
            }
        }

        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = { loginViewModel.loginEmailPassword(email.text, password.text) },
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.secondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(
                    text = "Login",
                    color = AppTheme.colors.onSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        ClickableText(
            text = AnnotatedString("Forgot password?"),
            onClick = { },
            style = TextStyle(
                fontSize = 14.sp,
                fontFamily = FontFamily.Default,
                color = AppTheme.colors.onSecondary
            )
        )
    }
}