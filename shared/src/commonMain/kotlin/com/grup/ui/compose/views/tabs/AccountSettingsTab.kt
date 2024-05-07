package com.grup.ui.compose.views.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.grup.ui.compose.views.AccountSettingsView

internal object AccountSettingsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.AccountCircle)
            return remember {
                TabOptions(
                    index = 3u,
                    title = "Account",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(AccountSettingsView())
    }
}