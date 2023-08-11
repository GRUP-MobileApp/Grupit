package com.grup.ui.compose.views

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.Group
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.compose.AcceptRejectRow
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.SimpleLazyListPage
import com.grup.ui.models.Notification
import com.grup.ui.viewmodel.MainViewModel
import com.grup.ui.viewmodel.NotificationsViewModel

internal class GroupNotificationsView : Screen {
    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            val notificationsViewModel: NotificationsViewModel =
                rememberScreenModel { NotificationsViewModel() }
            val navigator = LocalNavigator.currentOrThrow

            notificationsViewModel.logGroupNotificationsDate()
            GroupNotificationsLayout(
                notificationsViewModel = notificationsViewModel,
                navigator = navigator
            )
        }
    }

}

@Composable
private fun GroupNotificationsLayout(
    notificationsViewModel: NotificationsViewModel,
    navigator: Navigator
) {
    val selectedGroup: Group = MainViewModel.selectedGroup
    val notifications: Map<String, List<Notification>> by
        notificationsViewModel.notifications.collectAsStateWithLifecycle()

    SimpleLazyListPage(
        pageName = "Notifications",
        onBackPress = { navigator.pop() }
    ) {
        items(notifications[selectedGroup.id] ?: emptyList()) { notification ->
            val sideContent: (@Composable ColumnScope.() -> Unit)? =
                when (notification) {
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
                    is Notification.IncomingTransactionOnSettleAction -> {
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
                    else -> null
                }
            NotificationRowCard(
                notification = notification,
                sideContent = sideContent
            )
        }
    }
}

@Composable
private fun NotificationRowCard(
    notification: Notification,
    mainContent: @Composable ColumnScope.() -> Unit = {
        Caption(
            text = isoDate(notification.date),
            fontSize = 12.sp
        )
        H1Text(
            text = notification.displayText(),
            fontSize = 16.sp
        )
    },
    sideContent: (@Composable ColumnScope.() -> Unit)?,
) {
    UserInfoRowCard(
        userInfo = notification.userInfo,
        iconSize = 60.dp,
        mainContent = mainContent,
        sideContent = sideContent
    )
}
