package com.grup.ui.controllers

import androidx.compose.ui.window.ComposeUIViewController
import com.grup.platform.signin.GoogleSignInManager
import com.grup.ui.compose.DebugApplication

fun DebugApplicationController(
    googleSignInManager: GoogleSignInManager?
) = ComposeUIViewController {
    DebugApplication(googleSignInManager = googleSignInManager)
}
