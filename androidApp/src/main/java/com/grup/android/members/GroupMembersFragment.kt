package com.grup.android.members

import LoadingSpinner
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.R
import com.grup.android.ui.apptheme.AppTheme
import com.grup.android.ui.*

import com.grup.android.ui.SmallIcon
import com.grup.models.UserInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class GroupMembersFragment : Fragment() {
    private val groupMembersViewModel: GroupMembersViewModel by navGraphViewModels(R.id.main_graph)

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
                    GroupMembersLayout(
                        groupMembersViewModel = groupMembersViewModel,
                        navController = findNavController()
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalLifecycleComposeApi::class)
@Composable
fun GroupMembersLayout(
    groupMembersViewModel: GroupMembersViewModel,
    navController: NavController
) {
    val userInfoBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val addToGroupBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val userInfos: List<UserInfo> by groupMembersViewModel.userInfos.collectAsStateWithLifecycle()
    val inviteResult: GroupMembersViewModel.InviteResult by
        groupMembersViewModel.inviteResult.collectAsStateWithLifecycle()

    var usernameSearchQuery: String by remember { mutableStateOf("") }
    var addToGroupUsernameSearchQuery: String by remember { mutableStateOf("") }

    val openAddToGroupBottomSheet: () -> Unit = {
        addToGroupUsernameSearchQuery = ""
        groupMembersViewModel.resetInviteResult()
        scope.launch { addToGroupBottomSheetState.show() }
    }

    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
        AddToGroupBottomSheetLayout(
            addToGroupUsernameSearchQuery = addToGroupUsernameSearchQuery,
            onQueryChange = { addToGroupUsernameSearchQuery = it },
            state = addToGroupBottomSheetState,
            inviteResult = inviteResult,
            inviteUsernameToGroupOnClick = {
                groupMembersViewModel.inviteUserToGroup(addToGroupUsernameSearchQuery)
            }
        ) {
            GroupMemberInfoBottomSheet(state = userInfoBottomSheetState) {
                content()
            }
        }
    }

    modalSheets {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { h1Text(text = "Members", color = AppTheme.colors.onSecondary) },
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
                    },
                    actions = {
                        AddToGroupButton(
                            addToGroupOnClick = openAddToGroupBottomSheet
                        )
                    }
                )
            },
            backgroundColor = AppTheme.colors.primary,
            drawerContent = { Text(text = "drawerContent") }
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(AppTheme.shapes.large)
                    .padding(padding)
                    .background(AppTheme.colors.secondary)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(AppTheme.dimensions.paddingMedium)
                ) {
                    UsernameSearchBar(
                        usernameSearchQuery = usernameSearchQuery,
                        onQueryChange = { username ->
                            usernameSearchQuery = username
                            groupMembersViewModel.resetInviteResult()
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .padding(top = AppTheme.dimensions.paddingMedium)
                    )
                    UsersList(
                        userInfos = userInfos.filter { userInfo ->
                            userInfo.nickname!!.contains(usernameSearchQuery, ignoreCase = true)
                        },
                        scope = scope,
                        state = userInfoBottomSheetState
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun UsersList(
    userInfos: List<UserInfo>,
    scope: CoroutineScope,
    state: ModalBottomSheetState
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(userInfos) { _, userInfo ->
            UserInfoRowCard(
                userInfo = userInfo,
                onClick = {
                    scope.launch { state.show() }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupMemberInfoBottomSheet(
    state: ModalBottomSheetState,
    backgroundColor: Color = AppTheme.colors.primary,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppTheme.colors.secondary)
                        .padding(vertical = 10.dp, horizontal = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(98.dp)
                    )
                    h1Text(
                        text = "Member",
                        modifier = Modifier
                            .padding(top = AppTheme.dimensions.paddingLarge),
                        color = textColor,
                        fontSize = 50.sp
                    )
                    Divider()
                }
            /*TODO user transaction list here*/
            },
        sheetBackgroundColor = backgroundColor,
        content = content,
        sheetShape = AppTheme.shapes.large
    )
}

@Composable
fun AddToGroupButton(
    addToGroupOnClick: () -> Unit
) {
    IconButton(onClick = addToGroupOnClick) {
        SmallIcon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add to Group"
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddToGroupBottomSheetLayout(
    addToGroupUsernameSearchQuery: String,
    onQueryChange: (String) -> Unit,
    inviteUsernameToGroupOnClick: () -> Unit,
    inviteResult: GroupMembersViewModel.InviteResult,
    state: ModalBottomSheetState,
    backgroundColor: Color = AppTheme.colors.secondary,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    val usernameSearchBarBorderColor: Color =
        if (inviteResult is GroupMembersViewModel.InviteResult.Error) {
            AppTheme.colors.error
        } else {
            Color.Transparent
        }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.paddingMedium)
            ) {
                UsernameSearchBar(
                    usernameSearchQuery = addToGroupUsernameSearchQuery,
                    onQueryChange = onQueryChange,
                    border = usernameSearchBarBorderColor,
                    modifier = Modifier.padding(top = AppTheme.dimensions.paddingSmall)
                )
                Spacer(modifier = Modifier.height(AppTheme.dimensions.spacing))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (inviteResult is GroupMembersViewModel.InviteResult.Error) {
                        Text(text = inviteResult.exception.message!!)
                    } else if(inviteResult is GroupMembersViewModel.InviteResult.Sent) {
                        Text(text = "Sent!")
                    }
                }
                Spacer(modifier = Modifier.height(AppTheme.dimensions.spacing))
                Button(
                    onClick = inviteUsernameToGroupOnClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
                    shape = AppTheme.shapes.CircleShape
                ) {
                    if (inviteResult is GroupMembersViewModel.InviteResult.Pending) {
                        LoadingSpinner()
                    } else {
                        Text(text = "Add to group", color = textColor)
                    }
                }
            }
        },
        sheetBackgroundColor = backgroundColor,
        content = content
    )
}