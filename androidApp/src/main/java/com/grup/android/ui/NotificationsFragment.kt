package com.grup.android.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.*
import com.grup.android.R
import com.grup.ui.compose.views.NotificationsView
import com.grup.ui.viewmodel.NotificationsViewModel

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
                NotificationsView(
                    notificationsViewModel = notificationsViewModel,
                    navController = AndroidNavigationController(findNavController())
                )
            }
        }
    }
}
