package com.grup.android.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.*
import com.grup.android.R
import com.grup.android.ui.*
import com.grup.models.Group

class NotificationsFragment : Fragment() {
    private val notificationsViewModel:
            NotificationsViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        notificationsViewModel.logGroupNotificationsDate()
        return ComposeView(requireContext()).apply {
            setContent {
                NotificationsLayout(
                    notificationsViewModel = notificationsViewModel,
                    navController = findNavController()
                )
            }
        }
    }
}

@Composable
fun NotificationsLayout(
    notificationsViewModel: NotificationsViewModel,
    navController: NavController
) {
    val selectedGroup: Group = MainViewModel.selectedGroup
    val notifications: Map<String, List<Notification>> by
        notificationsViewModel.notifications.collectAsStateWithLifecycle()

    SimpleLazyListPage(
        pageName = "Notifications",
        onBackPress = { navController.popBackStack() }
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
fun NotificationRowCard(
    notification: Notification,
    mainContent: @Composable ColumnScope.() -> Unit = {
        Caption(text = isoDate(notification.date), fontSize = 12.sp)
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
