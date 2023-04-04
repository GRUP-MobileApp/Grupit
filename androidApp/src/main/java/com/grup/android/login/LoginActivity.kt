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
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
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

        val appUpdateManager = AppUpdateManagerFactory.create(applicationContext)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo
        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            if (
                appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.updatePriority() >= 3
                && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    AppUpdateType.IMMEDIATE,
                    this,
                    0
                )
            }
        }

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
}
