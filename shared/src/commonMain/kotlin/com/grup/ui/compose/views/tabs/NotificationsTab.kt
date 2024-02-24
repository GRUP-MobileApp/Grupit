package com.grup.ui.compose.views.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.grup.ui.compose.views.NotificationsView

internal object NotificationsTab : Tab {
    override val options: TabOptions
        @Composable
        get() {
            val icon = rememberVectorPainter(Icons.Default.Notifications)
            return remember {
                TabOptions(
                    index = 2u,
                    title = "Notifications",
                    icon = icon
                )
            }
        }

    @Composable
    override fun Content() {
        Navigator(NotificationsView())
    }
}