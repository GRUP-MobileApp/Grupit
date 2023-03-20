package com.grup.android.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.R
import com.grup.android.ui.SimpleLazyListPage
import com.grup.android.ui.apptheme.AppTheme
import com.grup.models.GroupInvite

class GroupInvitesFragment : Fragment() {
    private val groupInvitesViewModel: GroupInvitesViewModel by navGraphViewModels(R.id.main_graph)

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
                    GroupInvitesLayout(
                        groupInvitesViewModel = groupInvitesViewModel,
                        navController = findNavController()
                    )
                }
            }
        }
    }
}

@Composable
fun GroupInvitesLayout(
    groupInvitesViewModel: GroupInvitesViewModel,
    navController: NavController
) {
    val groupInvites: List<GroupInvite> by
        groupInvitesViewModel.groupInvites.collectAsStateWithLifecycle()

    SimpleLazyListPage(
        pageName = "Group Invites",
        onBackPress = { navController.popBackStack() }
    ) {
        items(groupInvites) { groupInvite ->

        }
    }
}
