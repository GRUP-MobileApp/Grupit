package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.LoadingSpinner
import com.grup.ui.compose.ModalBottomSheetLayout
import com.grup.ui.compose.SmallIcon
import com.grup.ui.compose.UserInfoRowCard
import com.grup.ui.compose.UsernameSearchBar
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoFullDate
import com.grup.ui.viewmodel.GroupMembersViewModel
import kotlinx.coroutines.launch

internal class GroupMembersView(private val groupId: String) : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            val groupMembersViewModel = rememberScreenModel { GroupMembersViewModel(groupId) }
            val navigator = LocalNavigator.currentOrThrow
            GroupMembersLayout(
                groupMembersViewModel = groupMembersViewModel,
                navigator = navigator
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GroupMembersLayout(
    groupMembersViewModel: GroupMembersViewModel,
    navigator: Navigator
) {
    val userInfoBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val addToGroupBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val userInfos: List<UserInfo> by groupMembersViewModel.userInfos.collectAsStateWithLifecycle()
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
                groupMembersViewModel.createGroupInvite(addToGroupUsernameSearchQuery)
            }
        ) {
            selectedUserInfo?.let { selectedUserInfo ->
                GroupMemberInfoBottomSheet(
                    selectedUserInfo = selectedUserInfo,
                    state = userInfoBottomSheetState
                ) {
                    content()
                }
            } ?: content()
        }
    }

    modalSheets {
        BackPressScaffold(
            title = "Members",
            onBackPress = { navigator.pop() },
            actions = { AddToGroupButton(addToGroupOnClick = openAddToGroupBottomSheet) }
        ) { padding ->
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(vertical = AppTheme.dimensions.cardPadding),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(AppTheme.dimensions.appPadding)
                    .clip(AppTheme.shapes.large)
                    .background(AppTheme.colors.secondary)
            ) {
                item {
                    UsernameSearchBar(
                        usernameSearchQuery = usernameSearchQuery,
                        onQueryChange = { username ->
                            usernameSearchQuery = username
                            groupMembersViewModel.resetInviteResult()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.dimensions.cardPadding)
                    )
                    Spacer(modifier = Modifier.height(AppTheme.dimensions.spacingSmall))
                }
                items(
                    userInfos.filter { userInfo ->
                        userInfo.user.displayName.contains(usernameSearchQuery, ignoreCase = true)
                    }
                ) { userInfo ->
                    UserInfoRowCard(
                        userInfo = userInfo,
                        mainContent = {
                            H1Text(text = userInfo.user.displayName)
                            Caption(text = "@${userInfo.user.venmoUsername}")
                        },
                        modifier = Modifier
                            .clickable { userInfoOnClick(userInfo) }
                            .padding(
                                horizontal = AppTheme.dimensions.cardPadding,
                                vertical = AppTheme.dimensions.spacingSmall
                            )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GroupMemberInfoBottomSheet(
    selectedUserInfo: UserInfo,
    state: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.appPadding)
            ) {
                with(selectedUserInfo) {
                    UserInfoRowCard(userInfo = this) {
                        H1Text(text = user.displayName)
                        Caption(text = "@${user.venmoUsername}")
                        Spacer(modifier = Modifier.height(AppTheme.dimensions.spacingSmall))
                        Caption(text = "Joined on ${isoFullDate(user.latestViewDate)}")
                    }
                }
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
                    labelText = "Search by username",
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
                        H1Text(text = inviteResult.exception.message!!)
                    } else if (inviteResult is GroupMembersViewModel.InviteResult.Sent) {
                        H1Text(text = "Sent!")
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
                        H1Text(text = "Add to group", color = textColor)
                    }
                }
            }
        },
        content = content
    )
}
