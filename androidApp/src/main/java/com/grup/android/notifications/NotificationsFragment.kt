package com.grup.android.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.MainViewModel
import com.grup.android.R
import com.grup.android.isoDate
import com.grup.android.ui.*
import com.grup.android.ui.apptheme.AppTheme
import com.grup.models.Group

class NotificationsFragment : Fragment() {
    private val notificationsViewModel:
            NotificationsViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colors.onSecondary
                ) {
                    NotificationsLayout(
                        notificationsViewModel = notificationsViewModel,
                        navController = findNavController()
                    )
                }
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
                when(notification) {
                    is Notification.IncomingDebtAction -> {
                        {
                            IconButton(
                                onClick = {
                                    notificationsViewModel.acceptDebtAction(
                                        notification.debtAction, notification.transactionRecord
                                    )
                                }
                            ) {
                                SmallIcon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Accept debt"
                                )
                            }
                        }
                    }
                    is Notification.IncomingTransactionOnSettleAction -> {
                        {
                            IconButton(
                                onClick = {
                                    notificationsViewModel.acceptSettleActionTransaction(
                                        notification.settleAction,
                                        notification.transactionRecord
                                    )
                                }
                            ) {
                                SmallIcon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Accept invite"
                                )
                            }
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
    sideContent: (@Composable () -> Unit)?,
    mainContent: @Composable ColumnScope.() -> Unit = {
        Caption(text = isoDate(notification.date), fontSize = 12.sp)
        H1Text(
            text = notification.displayText(),
            fontSize = 16.sp,
            modifier = Modifier.fillMaxWidth(if (sideContent == null) 1f else 0.85f)
        )
    }
) {
    UserInfoRowCard(
        userInfo = notification.userInfo,
        iconSize = 60.dp,
        mainContent = mainContent,
        sideContent = sideContent ?: { }
    )
}
