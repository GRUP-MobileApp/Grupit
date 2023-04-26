package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.grup.models.UserInfo
import com.grup.other.collectAsStateWithLifecycle
import com.grup.ui.*
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.UserInfoRowCard
import com.grup.ui.compose.UsernameSearchBar
import com.grup.ui.models.TransactionActivity
import com.grup.ui.viewmodel.MainViewModel
import com.grup.ui.viewmodel.GroupMembersViewModel
import kotlinx.coroutines.launch

@Composable
fun GroupMembersView(
    groupMembersViewModel: GroupMembersViewModel,
    mainViewModel: MainViewModel,
    navController: NavigationController
) {
    CompositionLocalProvider(
        LocalContentColor provides AppTheme.colors.onSecondary
    ) {
        GroupMembersLayout(
            groupMembersViewModel = groupMembersViewModel,
            mainViewModel = mainViewModel,
            navController = navController
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GroupMembersLayout(
    groupMembersViewModel: GroupMembersViewModel,
    mainViewModel: MainViewModel,
    navController: NavigationController
) {
    val userInfoBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val addToGroupBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val userInfos: List<UserInfo> by groupMembersViewModel.userInfos.collectAsStateWithLifecycle()
    val groupActivity: List<TransactionActivity>
        by mainViewModel.groupActivity.collectAsStateWithLifecycle()
    val inviteResult: GroupMembersViewModel.InviteResult by
        groupMembersViewModel.inviteResult.collectAsStateWithLifecycle()

    var usernameSearchQuery: String by remember { mutableStateOf("") }
    var addToGroupUsernameSearchQuery: String by remember { mutableStateOf("") }
    var selectedUserInfo: UserInfo? by remember { mutableStateOf(null) }

    val userInfoOnClick: (UserInfo) -> Unit = { userInfo ->
        selectedUserInfo = userInfo
        scope.launch { userInfoBottomSheetState.show() }
    }
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
            selectedUserInfo?.let { selectedUserInfo ->
                GroupMemberInfoBottomSheet(
                    selectedUserInfo = selectedUserInfo,
                    groupActivity = groupActivity.filter { transactionActivity ->
                        transactionActivity.userInfo.userId!! == selectedUserInfo.userId
                    },
                    state = userInfoBottomSheetState
                ) {
                    content()
                }
            } ?: content()

        }
    }

    modalSheets {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        H1Text(
                            text = "Members",
                            color = AppTheme.colors.onSecondary
                        )
                    },
                    backgroundColor = AppTheme.colors.primary,
                    navigationIcon = {
                        IconButton(
                            onClick = { navController.onBackPress() }
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
            backgroundColor = AppTheme.colors.primary
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
                        userInfoOnClick = { userInfoOnClick(it) }
                    )
                }
            }
        }
    }
}

@Composable
private fun UsersList(
    userInfos: List<UserInfo>,
    userInfoOnClick: (UserInfo) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(userInfos) { _, userInfo ->
            UserInfoRowCard(
                userInfo = userInfo,
                iconSize = 70.dp,
                modifier = Modifier.clickable { userInfoOnClick(userInfo) }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupMemberInfoBottomSheet(
    selectedUserInfo: UserInfo,
    groupActivity: List<TransactionActivity>,
    state: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    BackPressModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.5f)
                    .padding(top = AppTheme.dimensions.appPadding)
                    .padding(horizontal = AppTheme.dimensions.appPadding)
            ) {
                UserInfoRowCard(
                    userInfo = selectedUserInfo,
                    iconSize = 64.dp
                )
                Divider()
                RecentActivityList(groupActivity = groupActivity)
            }
        },
        content = content
    )
}

@Composable
private fun AddToGroupButton(
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
private fun AddToGroupBottomSheetLayout(
    addToGroupUsernameSearchQuery: String,
    onQueryChange: (String) -> Unit,
    inviteUsernameToGroupOnClick: () -> Unit,
    inviteResult: GroupMembersViewModel.InviteResult,
    state: ModalBottomSheetState,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    val usernameSearchBarBorderColor: Color =
        if (inviteResult is GroupMembersViewModel.InviteResult.Error) {
            AppTheme.colors.error
        } else {
            Color.Transparent
        }

    BackPressModalBottomSheetLayout(
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
                    } else if (inviteResult is GroupMembersViewModel.InviteResult.Sent) {
                        Text(text = "Sent!")
                    }
                }
                Spacer(modifier = Modifier.height(AppTheme.dimensions.spacing))
                Button(
                    onClick = inviteUsernameToGroupOnClick,
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
                    shape = AppTheme.shapes.circleShape
                ) {
                    if (inviteResult is GroupMembersViewModel.InviteResult.Pending) {
                        LoadingSpinner()
                    } else {
                        Text(text = "Add to group", color = textColor)
                    }
                }
            }
        },
        content = content
    )
}
