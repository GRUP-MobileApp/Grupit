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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.GroupInvite
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.getProfilePictureURI
import com.grup.ui.compose.isoDate
import com.grup.ui.compose.profilePicturePainter
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.IconRowCard
import com.grup.ui.compose.SimpleLazyListPage
import com.grup.ui.viewmodel.GroupInvitesViewModel

class GroupInvitesView() : Screen {
    @Composable
    override fun Content() {
        val groupInvitesViewModel: GroupInvitesViewModel =
            rememberScreenModel { GroupInvitesViewModel() }
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            GroupInvitesLayout(
                groupInvitesViewModel = groupInvitesViewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun GroupInvitesLayout(
    groupInvitesViewModel: GroupInvitesViewModel,
    navigator: Navigator
) {
    val groupInvites: List<GroupInvite> by
    groupInvitesViewModel.groupInvites.collectAsStateWithLifecycle()

    SimpleLazyListPage(
        pageName = "Group Invites",
        onBackPress = { navigator.pop() }
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
