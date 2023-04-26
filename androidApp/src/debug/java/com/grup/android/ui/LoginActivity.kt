package com.grup.android.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.grup.android.*
import com.grup.ui.compose.views.DebugLoginView
import com.grup.ui.viewmodel.LoginViewModel


class LoginActivity : AppCompatActivity() {
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Thread.setDefaultUncaughtExceptionHandler(ExceptionHandler(this))

        setContent {
            DebugLoginView(
                loginViewModel = loginViewModel,
                googleLoginOnClick = {
                    registerForActivityResult(
                        ActivityResultContracts.StartActivityForResult()
                    ) { result ->
                        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                        if (task.isSuccessful) {
                            val account: GoogleSignInAccount? =
                                task.getResult(ApiException::class.java)
                            val token: String = account?.idToken!!

                            loginViewModel.loginGoogleAccount(token)
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
