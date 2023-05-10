package com.grup.ui.compose

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import com.grup.platform.signin.GoogleSignInManager
import com.grup.ui.compose.views.DebugLoginView
import com.grup.ui.compose.views.ReleaseLoginView

@Composable
fun DebugApplication(googleSignInManager: GoogleSignInManager? = null) {
    Navigator(screen = DebugLoginView(googleSignInManager = googleSignInManager))
}

@Composable
fun ReleaseApplication(googleSignInManager: GoogleSignInManager? = null) {
    Navigator(screen = ReleaseLoginView(googleSignInManager = googleSignInManager))
}