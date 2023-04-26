package com.grup.ui.compose.views

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grup.models.GroupInvite
import com.grup.other.collectAsStateWithLifecycle
import com.grup.other.getProfilePictureURI
import com.grup.other.isoDate
import com.grup.other.profilePicturePainter
import com.grup.ui.*
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.IconRowCard
import com.grup.ui.compose.SimpleLazyListPage
import com.grup.ui.viewmodel.GroupInvitesViewModel

@Composable
fun GroupInvitesView(
    groupInvitesViewModel: GroupInvitesViewModel,
    navController: NavigationController
) {
    CompositionLocalProvider(
        LocalContentColor provides AppTheme.colors.onSecondary
    ) {
        GroupInvitesLayout(
            groupInvitesViewModel = groupInvitesViewModel,
            navController = navController
        )
    }
}

@Composable
private fun GroupInvitesLayout(
    groupInvitesViewModel: GroupInvitesViewModel,
    navController: NavigationController
) {
    val groupInvites: List<GroupInvite> by
    groupInvitesViewModel.groupInvites.collectAsStateWithLifecycle()

    SimpleLazyListPage(
        pageName = "Group Invites",
        onBackPress = { navController.onBackPress() }
    ) {
        items(groupInvites) { groupInvite ->
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                GroupInviteRowCard(
                    groupInvite = groupInvite,
                    acceptGroupInviteOnClick = {
                        groupInvitesViewModel.acceptGroupInvite(groupInvite)
                    },
                    rejectGroupInviteOnClick = {
                        groupInvitesViewModel.rejectGroupInvite(groupInvite)
                    }
                )
            }
        }
    }
}

@Composable
private fun GroupInviteRowCard(
    groupInvite: GroupInvite,
    acceptGroupInviteOnClick: () -> Unit,
    rejectGroupInviteOnClick: () -> Unit
) {
    val pfpPainter = profilePicturePainter(getProfilePictureURI(groupInvite.invitee!!))

    IconRowCard(
        painter = pfpPainter,
        mainContent = {
            Column(
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxHeight()
            ) {
                Caption(text = isoDate(groupInvite.date))
                H1Text(
                    text = "${groupInvite.inviterUsername!!} is inviting you to " +
                            "${groupInvite.groupName}",
                    fontSize = 16.sp
                )
            }
        },
        sideContent = {
            AcceptRejectRow(
                acceptOnClick = acceptGroupInviteOnClick,
                rejectOnClick = rejectGroupInviteOnClick
            )
        },
        iconSize = 60.dp
    )
}
