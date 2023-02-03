package com.grup.android.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.R
import com.grup.android.ui.apptheme.AppTheme

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

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun NotificationsLayout(
    notificationsViewModel: NotificationsViewModel,
    navController: NavController
) {
    val notifications:
            List<Notification> by notificationsViewModel.notifications.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", color = AppTheme.colors.onSecondary) },
                backgroundColor = AppTheme.colors.primary,
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back",
                            Modifier.background(AppTheme.colors.primary)
                        )
                    }
                }
            )
        },
        backgroundColor = AppTheme.colors.primary
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
            itemsIndexed(notifications) { _, notification ->
                when(notification) {
                    is Notification.IncomingGroupInvite -> Text(text = notification.displayText())
                    is Notification.InviteeAcceptOutgoingGroupInvite ->
                        Text(text = notification.displayText())
                    is Notification.IncomingDebtAction -> Text(text = notification.displayText())
                    is Notification.DebtorAcceptOutgoingDebtAction ->
                        Text(text = notification.displayText())
                    is Notification.NewSettleAction -> Text(text = notification.displayText())
                }
            }
        }
    }
}
