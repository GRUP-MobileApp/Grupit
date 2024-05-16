package com.grup.ui.compose.views.tabs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.grup.ui.compose.views.DebtActionDetailsView
import com.grup.ui.compose.views.GroupDetailsView
import com.grup.ui.compose.views.GroupsView
import com.grup.ui.compose.views.SettleActionDetailsView

internal class GroupsTab(
    private val groupId: String? = null,
    private val action: Pair<String, String>? = null
) : Tab {
    override val key: ScreenKey = uniqueScreenKey

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
        Navigator(
            mutableListOf<Screen>(GroupsView()).apply {
                groupId?.let { groupId ->
                    add(GroupDetailsView(groupId))
                    action?.let { (actionType, actionId) ->
                        when (actionType) {
                            "DebtAction" -> add(DebtActionDetailsView(actionId))
                            "SettleAction" -> add(SettleActionDetailsView(actionId))
                        }

                    }
                }
            }
        ) { navigator ->
            SlideTransition(navigator = navigator) { screen ->
                screen.Content()
            }
        }
    }
}
