package com.grup.ui

import androidx.compose.ui.window.ComposeUIViewController
import com.grup.ui.compose.Application

fun DebugApplicationController() = ComposeUIViewController { Application(isDebug = true) }
fun ReleaseApplicationController() = ComposeUIViewController { Application() }
