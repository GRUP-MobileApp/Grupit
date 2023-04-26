package com.grup.ui.compose.views

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grup.models.Group
import com.grup.other.collectAsStateWithLifecycle
import com.grup.other.isoDate
import com.grup.ui.*
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.compose.AcceptRejectRow
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.SimpleLazyListPage
import com.grup.ui.models.Notification
import com.grup.ui.viewmodel.MainViewModel
import com.grup.ui.viewmodel.NotificationsViewModel

@Composable
fun NotificationsView(
    notificationsViewModel: NotificationsViewModel,
    navController: NavigationController
) {
    CompositionLocalProvider(
        LocalContentColor provides AppTheme.colors.onSecondary
    ) {
        NotificationsLayout(
            notificationsViewModel = notificationsViewModel,
            navController = navController
        )
    }
}

@Composable
internal fun NotificationsLayout(
    notificationsViewModel: NotificationsViewModel,
    navController: NavigationController
) {
    val selectedGroup: Group = MainViewModel.selectedGroup
    val notifications: Map<String, List<Notification>> by
        notificationsViewModel.notifications.collectAsStateWithLifecycle()

    SimpleLazyListPage(
        pageName = "Notifications",
        onBackPress = { navController.onBackPress() }
    ) {
        items(notifications[selectedGroup.getId()] ?: emptyList()) { notification ->
            val sideContent: (@Composable () -> Unit)? =
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
internal fun NotificationRowCard(
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
    sideContent: (@Composable () -> Unit)?,
) {
    UserInfoRowCard(
        userInfo = notification.userInfo,
        iconSize = 60.dp,
        mainContent = mainContent,
        sideContent = sideContent
    )
}
