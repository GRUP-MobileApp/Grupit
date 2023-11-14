package com.grup.ui.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.grup.ui.apptheme.AppTheme

internal class MainView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    private object GroupsTab : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val icon = rememberVectorPainter(Icons.Default.Home)
                return remember {
                    TabOptions(
                        index = 1u,
                        title = "Groups",
                        icon = icon
                    )
                }
            }

        @Composable
        override fun Content() {
            Navigator(GroupsView())
        }
    }

    private object NotificationsTab : Tab {
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

    private object AccountSettingsTab : Tab {
        override val options: TabOptions
            @Composable
            get() {
                val icon = rememberVectorPainter(Icons.Default.Settings)
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

    private val tabs = listOf(GroupsTab, NotificationsTab, AccountSettingsTab)

    @OptIn(ExperimentalVoyagerApi::class)
    @Composable
    override fun Content() {
        TabNavigator(
            GroupsTab,
            tabDisposable = {
                TabDisposable(navigator = it, tabs = tabs)
            }
        ) {
            Scaffold(
                backgroundColor = AppTheme.colors.primary,
                bottomBar = {
                    BottomNavigation(
                        backgroundColor = AppTheme.colors.secondary
                    ) {
                        tabs.forEach { TabNavigationItem(it) }
                    }
                }
            ) { padding ->
                Box(modifier = Modifier.padding(padding)) {
                    CurrentTab()
                }
            }
        }
    }

    @Composable
    private fun RowScope.TabNavigationItem(tab: Tab) {
        val tabNavigator = LocalTabNavigator.current

        BottomNavigationItem(
            selected = tabNavigator.current.key == tab.key,
            onClick = { tabNavigator.current = tab },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
            selectedContentColor = AppTheme.colors.confirm,
            unselectedContentColor = AppTheme.colors.onPrimary
        )
    }
}