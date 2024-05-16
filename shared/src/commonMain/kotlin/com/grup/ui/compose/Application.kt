package com.grup.ui.compose

import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import cafe.adriel.voyager.navigator.Navigator
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.views.MainView
import com.grup.ui.compose.views.StartView
import com.grup.ui.compose.views.WelcomeView
import io.kamel.core.config.DefaultHttpCacheSize
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig

@Composable
fun Application(isDebug: Boolean = false) {
    val kamelConfig = KamelConfig {
        takeFrom(KamelConfig.Default)
        httpFetcher {
            httpCache(DefaultHttpCacheSize * 3)
        }
    }

    AppTheme {
        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary,
            LocalKamelConfig provides kamelConfig
        ) {
            Navigator(
                screen = StartView(isDebug = isDebug),
                onBackPressed = { currentScreen ->
                    when (currentScreen) {
                        is MainView -> false
                        is WelcomeView -> false
                        else -> true
                    }
                }
            )
        }
    }
}
