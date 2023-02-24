package com.grup.android.login

import LoadingSpinner
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender.SendIntentException
import android.net.Credentials
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.app.ActivityCompat.startIntentSenderForResult
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.identity.GetSignInIntentRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.grup.android.ExceptionHandler
import com.grup.android.MainActivity
import com.grup.android.ui.apptheme.AppTheme
import kotlinx.coroutines.runBlocking


class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        // Set up Google Sign-In options
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()
        val googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            AppTheme {
                LoginPage(
                    loginViewModel = loginViewModel,
                    googleSignInClient = googleSignInClient
                )
            }
        }

    }

}

@Composable
fun LoginPage(
    loginViewModel: LoginViewModel,
    googleSignInClient: GoogleSignInClient
) {
    var email: TextFieldValue by remember { mutableStateOf(TextFieldValue()) }
    var password: TextFieldValue by remember { mutableStateOf(TextFieldValue()) }
    val context = LocalContext.current

    val loginResult:
            LoginViewModel.LoginResult by loginViewModel.loginResult.collectAsStateWithLifecycle()

    if (loginResult is LoginViewModel.LoginResult.Success) {
        context.startActivity(Intent(context, MainActivity::class.java))
    }

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
        Text(
            text = "Login",
            style = TextStyle(fontSize = 40.sp, fontFamily = FontFamily.Monospace),
            color = AppTheme.colors.onSecondary
        )

        Spacer(modifier = Modifier.height(50.dp))

        TextField(
            label = { Text(text = "Username", color = AppTheme.colors.onSecondary) },
            modifier = Modifier.background(AppTheme.colors.secondary),
            textStyle = TextStyle(color = AppTheme.colors.onSecondary),
            value = email,
            onValueChange = { email = it },
            singleLine = true
        )

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            label = { Text(text = "Password", color = AppTheme.colors.onSecondary) },
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
                    onClick = { loginViewModel.registerEmailPassword(email.text, password.text) },
                    shape = AppTheme.shapes.CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.colors.secondary
                    ),
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                ) {
                    if (loginResult is LoginViewModel.LoginResult.PendingRegister) {
                        LoadingSpinner()
                    } else {
                        Text(
                            text = "Sign Up",
                            color = AppTheme.colors.onSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                Button(
                    onClick = { loginViewModel.loginEmailPassword(email.text, password.text) },
                    shape = AppTheme.shapes.CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.colors.confirm
                    ),
                    modifier = Modifier
                        .width(120.dp)
                        .height(50.dp)
                ) {
                    if (loginResult is LoginViewModel.LoginResult.PendingLogin) {
                        LoadingSpinner()
                    } else {
                        Text(
                            text = "Login",
                            color = AppTheme.colors.onSecondary
                        )
                    }
                }
            }
        }
        GoogleSignInButton(loginViewModel, googleSignInClient)
    }
}

@Composable
fun GoogleSignInButton(loginViewModel: LoginViewModel, googleSignInClient: GoogleSignInClient) {
    val context = LocalContext.current
    val signInLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                // Signed in successfully
                loginViewModel.handleSignInResult(account)
            } catch (e: ApiException) {
                // Sign in failed
            }
        }
    }

    Button(
        onClick = {
            val signInIntent = googleSignInClient.signInIntent
            signInLauncher.launch(signInIntent)
        },
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = Color(0xFF4285F4),
            contentColor = Color.White
        )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_logo_google),
            contentDescription = ""
        )
        Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
    }

}