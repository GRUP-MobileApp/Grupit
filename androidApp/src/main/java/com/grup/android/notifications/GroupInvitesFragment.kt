package com.grup.android.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import coil.compose.rememberAsyncImagePainter
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.grup.android.R
import com.grup.android.applyCachingAndBuild
import com.grup.android.getProfilePictureURI
import com.grup.android.isoDate
import com.grup.android.ui.*
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
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                GroupInviteRowCard(
                    groupInvite = groupInvite,
                    acceptGroupInviteOnClick = {
                        groupInvitesViewModel.acceptGroupInvite(groupInvite)
                    }
                )
            }
        }
    }
}

@Composable
fun GroupInviteRowCard(
    groupInvite: GroupInvite,
    acceptGroupInviteOnClick: () -> Unit
) {
    val context = LocalContext.current
    val imageRequest: ImageRequest =
        ImageRequest.Builder(context)
            .data(getProfilePictureURI(groupInvite.invitee!!))
            .applyCachingAndBuild(groupInvite.invitee!!)
    val pfpPainter = rememberAsyncImagePainter(
        model = imageRequest,
        imageLoader = context.imageLoader
    )
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
            AcceptCheckButton(onClick = acceptGroupInviteOnClick)
        },
        iconSize = 50.dp
    )
}
