package com.grup.ui.compose

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.views.MainView
import com.grup.ui.compose.views.StartView
import com.grup.ui.compose.views.WelcomeView

@Composable
fun Application(isDebug: Boolean = false) {
    AppTheme {
        Navigator(
            screen = StartView(isDebug = isDebug),
            onBackPressed = { currentScreen ->
                when(currentScreen) {
                    is MainView -> false
                    is WelcomeView -> false
                    else -> true
                }
            }
        )
    }
}
