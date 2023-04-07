package com.grup.android.login

import LoadingSpinner
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.grup.android.ExceptionHandler
import com.grup.android.MainActivity
import com.grup.android.R
import com.grup.android.ui.H1Text
import com.grup.android.ui.apptheme.AppTheme

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class LoginActivity : AppCompatActivity(), KoinComponent {

    private val loginViewModel: LoginViewModel by viewModels()
    private val googleSignInClient: GoogleSignInClient by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        googleSignInClient.silentSignIn().addOnCompleteListener { task ->
            loginViewModel.loginGoogleAccount(task)
        }

        setContent {
            AppTheme {
                LoginPage(
                    loginViewModel = loginViewModel,
                    googleSignInClient = googleSignInClient,
                    loginOnClick = {
                        startActivity(Intent(applicationContext, MainActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun LoginPage(
    loginViewModel: LoginViewModel,
    googleSignInClient: GoogleSignInClient,
    loginOnClick: () -> Unit
) {
    var email: TextFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var password: TextFieldValue by remember { mutableStateOf(TextFieldValue()) }
    val googleSignInLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            loginViewModel.loginGoogleAccount(
                GoogleSignIn.getSignedInAccountFromIntent(result.data)
            )
        }

    val loginResult:
            LoginViewModel.LoginResult by loginViewModel.loginResult.collectAsStateWithLifecycle()

    if (loginResult is LoginViewModel.LoginResult.Success) {
        loginOnClick()
    }

    val pendingLogin: Boolean =
        loginResult is LoginViewModel.LoginResult.PendingLogin ||
        loginResult is LoginViewModel.LoginResult.PendingRegister ||
        loginResult is LoginViewModel.LoginResult.PendingGoogleLogin


    Box(modifier = Modifier
        .fillMaxSize()
        .background(AppTheme.colors.primary)) {

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
                H1Text(text = "Username", color = AppTheme.colors.onSecondary, fontSize = 20.sp)
            },
            modifier = Modifier.background(AppTheme.colors.secondary),
            textStyle = TextStyle(color = AppTheme.colors.onSecondary),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            value = email,
            onValueChange = { email = it },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = {
                H1Text(text = "Password", color = AppTheme.colors.onSecondary, fontSize = 20.sp)
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
                    if (loginResult is LoginViewModel.LoginResult.PendingRegister) {
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
                    if (loginResult is LoginViewModel.LoginResult.PendingLogin) {
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
        Button(
            onClick = {
                if (!pendingLogin) {
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
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
                    painter = painterResource(id = R.drawable.ic_logo_google),
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

// test crash reports
@Composable
fun CrashButton() {
    Button(
        onClick = {
            throw RuntimeException("Test Crash")
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color.Black,
            contentColor = Color.White
        )
    ) {
        Text(text = "TEST CRASH", modifier = Modifier.padding(6.dp))
    }
}