package com.grup.android.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.registerForActivityResult
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.grup.android.ExceptionHandler
import com.grup.android.GOOGLE_WEB_CLIENT_ID
import com.grup.ui.compose.views.ReleaseLoginView
import com.grup.ui.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        AppUpdateManagerFactory.create(applicationContext).let { appUpdateManager ->
            appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
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
        }

        val loginGoogleAccount: (Task<GoogleSignInAccount>) -> Unit = { task ->
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                val token: String = account?.idToken!!

                loginViewModel.loginGoogleAccount(token)
            }
        }

        val googleSignInClient: GoogleSignInClient =
            GoogleSignIn.getClient(
                this,
                GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                    .build()
            )
        googleSignInClient.silentSignIn().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                val token: String = account?.idToken!!

                loginViewModel.loginGoogleAccount(token)
            }
        }

        val googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            loginGoogleAccount(GoogleSignIn.getSignedInAccountFromIntent(result.data))
        }

        setContent {
            ReleaseLoginView(
                loginViewModel = loginViewModel,
                googleLoginOnClick = {
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                },
                loginOnClick = {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}
