package com.grup.ui.controllers

import androidx.compose.ui.window.ComposeUIViewController
import com.grup.platform.signin.AuthManager
import com.grup.ui.compose.Application

fun DebugApplicationController() = ComposeUIViewController { Application(isDebug = true) }
