package com.grup.ui.compose

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.grup.platform.signin.AuthManager
import com.grup.platform.signin.GoogleSignInManager
import com.grup.ui.compose.views.DebugLoginView
import com.grup.ui.compose.views.MainView
import com.grup.ui.compose.views.ReleaseLoginView
import com.grup.ui.compose.views.StartView
import com.grup.ui.compose.views.WelcomeView

@Composable
fun Application(authManager: AuthManager = AuthManager(), isDebug: Boolean = false) {
    Navigator(
        screen = StartView(authManager = authManager, isDebug = isDebug),
        onBackPressed = { currentScreen ->
            when(currentScreen) {
                is MainView -> false
                is WelcomeView -> false
                else -> true
            }
        }
    )
}
