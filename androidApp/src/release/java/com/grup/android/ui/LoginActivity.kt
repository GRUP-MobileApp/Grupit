package com.grup.android.login

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
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
import com.grup.android.MainActivity
import com.grup.ui.compose.ReleaseLoginView
import com.grup.ui.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

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

        val googleSignInClient: GoogleSignInClient =
            GoogleSignIn.getClient(
                this,
                GoogleSignInOptions
                    .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(GOOGLE_WEB_CLIENT_ID)
                    .build()
            )
        val loginGoogleAccount: (Task<GoogleSignInAccount>) -> Unit = { task ->
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                val token: String = account?.idToken!!

                loginViewModel.loginGoogleAccount(token)
            }
        }

        googleSignInClient.silentSignIn().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val account: GoogleSignInAccount? = task.getResult(ApiException::class.java)
                val token: String = account?.idToken!!

                loginViewModel.loginGoogleAccount(token)
            }
        }

        setContent {
            ReleaseLoginView(
                loginViewModel = loginViewModel,
                googleLoginOnClick = {
                    registerForActivityResult(
                        ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        GoogleSignIn.getSignedInAccountFromIntent(result.data).let { task ->
                            loginGoogleAccount(task)
                        }
                    }
                },
                loginOnClick = {
                    startActivity(Intent(applicationContext, MainActivity::class.java))
                    finish()
                }
            )
        }
    }
}
