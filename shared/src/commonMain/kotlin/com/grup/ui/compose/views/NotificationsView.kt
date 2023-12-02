package com.grup.ui.compose.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.AcceptRejectRow
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.UserRowCard
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.compose.isoTime
import com.grup.ui.models.Notification
import com.grup.ui.viewmodel.NotificationsViewModel

internal class NotificationsView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            val notificationsViewModel = getScreenModel<NotificationsViewModel>()
            val navigator = LocalNavigator.currentOrThrow

            notificationsViewModel.logGroupNotificationsDate()
            CompositionLocalProvider(
                LocalContentColor provides AppTheme.colors.onSecondary
            ) {
                NotificationsLayout(
                    notificationsViewModel = notificationsViewModel,
                    navigator = navigator
                )
            }
        }
    }

}

@Composable
private fun NotificationsLayout(
    notificationsViewModel: NotificationsViewModel,
    navigator: Navigator
) {
    val notifications: List<Notification> by
        notificationsViewModel.notifications.collectAsStateWithLifecycle()

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
        modifier = Modifier
            .fillMaxWidth()
    ) {
        item {
            H1Text(
                text = "Notifications",
                color = AppTheme.colors.onSecondary,
                modifier = Modifier.fillMaxWidth(0.95f)
            )
        }
        items(
            notifications
        ) { notification ->
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
                    is Notification.IncomingSettleAction -> {
                        {
                            AcceptRejectRow(
                                acceptOnClick = {
                                    notificationsViewModel.acceptSettleAction(
                                        notification.settleAction,
                                        notification.transactionRecord
                                    )
                                },
                                rejectOnClick = {
                                    notificationsViewModel.rejectSettleAction(
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
            UserRowCard(
                user = notification.user,
                iconSize = 60.dp,
                mainContent = {
                    H1Text(
                        text = notification.group.groupName,
                        fontWeight = FontWeight.Medium,
                    )
                    H1Text(
                        text = notification.displayText(),
                        fontSize = AppTheme.typography.smallFont
                    )
                    Caption(
                        text = "${isoDate(notification.date)} at ${isoTime(notification.date)}",
                        fontSize = AppTheme.typography.tinyFont,
                        modifier = Modifier.padding(vertical = AppTheme.dimensions.spacingSmall)
                    )
                },
                sideContent = sideContent
            )
        }
    }
}
