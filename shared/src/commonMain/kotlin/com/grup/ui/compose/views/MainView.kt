package com.grup.ui.compose.views

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabDisposable
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.views.tabs.AccountSettingsTab
import com.grup.ui.compose.views.tabs.GroupsTab
import com.grup.ui.compose.views.tabs.NotificationsTab

internal class MainView : Screen {
    internal companion object {
        val tabs = mutableStateListOf(GroupsTab(), NotificationsTab, AccountSettingsTab)
    }

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        TabNavigator(
            tabs.first(),
            tabDisposable = { TabDisposable(navigator = it, tabs = tabs) }
        ) {
            Scaffold(
                backgroundColor = AppTheme.colors.primary,
                bottomBar = {
                    BottomNavigation(
                        backgroundColor = AppTheme.colors.secondary
                    ) {
                        tabs.indices.forEach { TabNavigationItem(it) }
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
    private fun RowScope.TabNavigationItem(index: Int) {
        val tabNavigator = LocalTabNavigator.current
        val tab = tabs[index]

        BottomNavigationItem(
            label = { H1Text(text = tab.options.title, fontSize = AppTheme.typography.tinyFont) },
            selected = tabNavigator.current.key == tab.key,
            onClick = {
                if (tabNavigator.current is GroupsTab && tab is GroupsTab) {
                    tabNavigator.current = GroupsTab()
                    tabs[0] = tabNavigator.current
                } else {
                    tabNavigator.current = tab
                }
            },
            icon = { Icon(painter = tab.options.icon!!, contentDescription = tab.options.title) },
            selectedContentColor = AppTheme.colors.confirm,
            unselectedContentColor = AppTheme.colors.onPrimary
        )
    }
}