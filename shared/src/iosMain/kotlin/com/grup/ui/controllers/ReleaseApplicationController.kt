package com.grup.ui.controllers

import androidx.compose.ui.window.ComposeUIViewController
import com.grup.platform.signin.GoogleSignInManager
import com.grup.ui.compose.ReleaseApplication
import com.grup.ui.compose.views.MainView

fun ReleaseApplicationController(
    googleSignInManager: GoogleSignInManager
) = ComposeUIViewController {
    ReleaseApplication(googleSignInManager = googleSignInManager)
}
