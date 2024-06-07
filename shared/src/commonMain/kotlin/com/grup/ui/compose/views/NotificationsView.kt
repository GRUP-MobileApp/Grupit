package com.grup.ui.compose.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.TabNavigator
import com.grup.models.Action
import com.grup.models.DebtAction
import com.grup.models.SettleAction
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.AcceptRejectButtons
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1Header
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.UserRowCard
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.compose.isoTime
import com.grup.ui.compose.views.tabs.GroupsTab
import com.grup.ui.models.Notification
import com.grup.ui.viewmodel.NotificationsViewModel
import kotlinx.coroutines.launch

internal class NotificationsView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val notificationsViewModel = rememberScreenModel { NotificationsViewModel() }
        val tabNavigator = LocalTabNavigator.current

        notificationsViewModel.logGroupNotificationsDate()

        NotificationsLayout(
            notificationsViewModel = notificationsViewModel,
            tabNavigator = tabNavigator
        )
    }
}

@Composable
private fun NotificationsLayout(
    notificationsViewModel: NotificationsViewModel,
    tabNavigator: TabNavigator
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    val notifications: List<Notification> by
        notificationsViewModel.notifications.collectAsStateWithLifecycle()

    val showErrorMessage: (String) -> Unit = { message ->
        scope.launch {
            scaffoldState.snackbarHostState.showSnackbar(message)
        }
    }

    val navigateAction: (Action) -> Unit = { action ->
        GroupsTab(
            action.userInfo.group.id,
            Pair(
                when(action) {
                    is DebtAction -> "DebtAction"
                    is SettleAction -> "SettleAction"
                },
                action.id
            )
        ).let { tab ->
            MainView.tabs[0] = tab
            tabNavigator.current = tab
        }
    }

    val navigateGroupsView: () -> Unit = {
        GroupsTab().let {
            MainView.tabs[0] = it
            tabNavigator.current = it
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        backgroundColor = AppTheme.colors.primary
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = AppTheme.dimensions.appPadding),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                H1Header(
                    text = "Notifications",
                    color = AppTheme.colors.onSecondary,
                    modifier = Modifier.fillMaxWidth()
                        .padding(start = AppTheme.dimensions.appPadding)
                )
            }
            items(notifications) { notification ->
                UserRowCard(
                    modifier = Modifier
                        .run {
                            when (notification) {
                                is Notification.IncomingDebtAction -> {
                                    clickable { navigateAction(notification.debtAction) }
                                }

                                is Notification.DebtorAcceptOutgoingDebtAction -> {
                                    clickable { navigateAction(notification.debtAction) }
                                }

                                is Notification.NewSettleAction -> {
                                    clickable { navigateAction(notification.settleAction) }
                                }

                                is Notification.IncomingSettleActionTransaction -> {
                                    clickable { navigateAction(notification.settleAction) }
                                }

                                is Notification.DebteeAcceptOutgoingSettleActionTransaction -> {
                                    clickable { navigateAction(notification.settleAction) }
                                }

                                else -> {
                                    this
                                }
                            }
                        }.padding(
                            horizontal = AppTheme.dimensions.appPadding,
                            vertical = AppTheme.dimensions.paddingMedium
                        ),
                    user = notification.user,
                    mainContent = {
                        H1Text(
                            text = notification.group.groupName,
                            fontWeight = FontWeight.Medium,
                        )
                        H1Text(
                            text = notification.displayText(),
                            fontSize = AppTheme.typography.smallFont,
                            maxLines = 2
                        )
                        Spacer(modifier = Modifier.height(AppTheme.dimensions.spacingSmall))
                        Caption(
                            text = "${isoDate(notification.date)} at ${isoTime(notification.date)}",
                            fontSize = AppTheme.typography.tinyFont,
                        )
                    },
                    sideContent = when (notification) {
                        is Notification.IncomingGroupInvite -> {
                            {
                                AcceptRejectButtons(
                                    acceptOnClick = {
                                        notificationsViewModel.acceptGroupInvite(
                                            notification.groupInvite,
                                            onSuccess = navigateGroupsView,
                                            onError = { it?.let(showErrorMessage) }
                                        )
                                    },
                                    rejectOnClick = {
                                        notificationsViewModel.rejectGroupInvite(
                                            notification.groupInvite,
                                            onSuccess = { },
                                            onError = { it?.let(showErrorMessage) }
                                        )
                                    }
                                )
                            }
                        }
                        else -> null
                    }
                )
            }
        }
    }
}
