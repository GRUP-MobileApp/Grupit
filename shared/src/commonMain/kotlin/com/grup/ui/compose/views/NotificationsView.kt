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
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.grup.models.Action
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.AcceptRejectRow
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

internal class NotificationsView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val notificationsViewModel = rememberScreenModel { NotificationsViewModel() }
        val tabNavigator = LocalTabNavigator.current

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            notificationsViewModel.logGroupNotificationsDate()
            CompositionLocalProvider(
                LocalContentColor provides AppTheme.colors.onSecondary
            ) {
                NotificationsLayout(
                    notificationsViewModel = notificationsViewModel,
                    actionOnClick = { action ->
                        GroupsTab(
                            action.userInfo.group.id,
                            action.id
                        ).let { tab ->
                            MainView.tabs[0] = tab
                            tabNavigator.current = tab
                        }
                    }
                )
            }
        }
    }

}

@Composable
private fun NotificationsLayout(
    notificationsViewModel: NotificationsViewModel,
    actionOnClick: (Action) -> Unit
) {
    val notifications: List<Notification> by
        notificationsViewModel.notifications.collectAsStateWithLifecycle()

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
                modifier = Modifier.fillMaxWidth().padding(start = AppTheme.dimensions.appPadding)
            )
        }
        items(notifications) { notification ->
            UserRowCard(
                modifier = Modifier
                    .run {
                        when(notification) {
                            is Notification.IncomingDebtAction -> {
                                clickable { actionOnClick(notification.debtAction) }
                            }
                            is Notification.DebtorAcceptOutgoingDebtAction -> {
                                clickable { actionOnClick(notification.debtAction) }
                            }
                            is Notification.NewSettleAction -> {
                                clickable { actionOnClick(notification.settleAction) }
                            }
                            is Notification.IncomingSettleActionTransaction -> {
                                clickable { actionOnClick(notification.settleAction) }
                            }
                            is Notification.DebteeAcceptOutgoingSettleActionTransaction -> {
                                clickable { actionOnClick(notification.settleAction) }
                            }
                            else -> { this }
                        }
                    }.padding(
                        horizontal = AppTheme.dimensions.appPadding,
                        vertical = AppTheme.dimensions.paddingMedium
                    ),
                user = notification.user,
                iconSize = 60.dp,
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
                    is Notification.IncomingDebtAction -> {
                        {
                            AcceptRejectRow(
                                acceptOnClick = {
                                    notificationsViewModel.acceptDebtAction(
                                        notification.debtAction,
                                        notification.transactionRecord
                                    )
                                },
                                rejectOnClick = {
                                    notificationsViewModel.rejectDebtAction(
                                        notification.debtAction,
                                        notification.transactionRecord
                                    )
                                }
                            )
                        }
                    }
                    is Notification.IncomingSettleActionTransaction -> {
                        {
                            AcceptRejectRow(
                                acceptOnClick = {
                                    notificationsViewModel.acceptSettleActionTransaction(
                                        notification.settleAction,
                                        notification.transactionRecord
                                    )
                                },
                                rejectOnClick = {
                                    notificationsViewModel.rejectSettleActionTransaction(
                                        notification.settleAction,
                                        notification.transactionRecord
                                    )
                                }
                            )
                        }
                    }
                    is Notification.IncomingGroupInvite -> {
                        {
                            AcceptRejectRow(
                                acceptOnClick = {
                                    notificationsViewModel.acceptGroupInvite(
                                        notification.groupInvite
                                    )
                                },
                                rejectOnClick = {
                                    notificationsViewModel.rejectGroupInvite(
                                        notification.groupInvite
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
