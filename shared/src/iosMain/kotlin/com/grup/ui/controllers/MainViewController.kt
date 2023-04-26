package com.grup.ui.controllers

import androidx.compose.ui.window.ComposeUIViewController
import com.grup.ui.NavigationController
import com.grup.ui.compose.views.MainView
import com.grup.ui.viewmodel.MainViewModel

fun MainViewController(
    mainViewModel: MainViewModel,
    navController: NavigationController,
    returnToLoginOnClick: () -> Unit
) = ComposeUIViewController { MainView(mainViewModel, navController, returnToLoginOnClick) }
