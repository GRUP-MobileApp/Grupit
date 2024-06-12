package com.grup.ui

import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.ui.window.ComposeUIViewController
import com.grup.ui.compose.Application

@OptIn(ExperimentalComposeApi::class)
fun ApplicationController(isDebug: Boolean = false) =
    ComposeUIViewController(configure = { opaque = false }) {
        Application(isDebug = isDebug)
    }
