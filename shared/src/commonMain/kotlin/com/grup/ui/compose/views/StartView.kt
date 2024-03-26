package com.grup.ui.compose.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.library.MR
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.viewmodel.StartViewModel
import dev.icerock.moko.resources.compose.painterResource

internal class StartView(
    private val isDebug: Boolean
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val startViewModel = rememberScreenModel { StartViewModel() }

        LaunchedEffect(true) {
            startViewModel.silentSignIn(isDebug = isDebug)
        }

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            StartLayout(
                startViewModel = startViewModel,
                navigator = navigator,
                isDebug = isDebug
            )
        }
    }
}

@Composable
private fun StartLayout(
    startViewModel: StartViewModel,
    navigator: Navigator,
    isDebug: Boolean
) {
    val silentSignInResult: StartViewModel.SilentSignInResult
        by startViewModel.silentSignInResult.collectAsStateWithLifecycle()

    when(silentSignInResult) {
        is StartViewModel.SilentSignInResult.NotSignedIn -> {
            navigator.push(
                if (isDebug) {
                    DebugLoginView()
                } else {
                    ReleaseLoginView()
                }
            )
            startViewModel.consumeSilentSignInResult()
        }
        is StartViewModel.SilentSignInResult.SignedIn -> {
            navigator.push(MainView())
            startViewModel.consumeSilentSignInResult()
        }
        is StartViewModel.SilentSignInResult.SignedInWelcomeSlideshow -> {
            navigator.push(listOf(MainView(), WelcomeView()))
            startViewModel.consumeSilentSignInResult()
        }
        is StartViewModel.SilentSignInResult.Error -> {
            // TODO: Handle error
            navigator.push(
                if (isDebug) {
                    DebugLoginView()
                } else {
                    ReleaseLoginView()
                }
            )
            startViewModel.consumeSilentSignInResult()
        }
        is StartViewModel.SilentSignInResult.None -> { }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize().background(AppTheme.colors.primary)
    ) {
        Image(
            painter = painterResource(MR.images.grup_logo),
            contentDescription = "GRUP logo",
            modifier = Modifier.fillMaxWidth(0.4f)
        )
    }
}