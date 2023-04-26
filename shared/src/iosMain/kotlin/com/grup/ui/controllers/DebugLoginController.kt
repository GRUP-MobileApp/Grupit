package com.grup.ui.controllers

import androidx.compose.ui.window.ComposeUIViewController
import com.grup.ui.compose.views.DebugLoginView
import com.grup.ui.viewmodel.LoginViewModel

fun DebugLoginController(
    loginViewModel: LoginViewModel,
    googleLoginOnClick: (() -> Unit)?,
    loginOnClick: () -> Unit
) = ComposeUIViewController { DebugLoginView(loginViewModel, googleLoginOnClick, loginOnClick) }
