package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
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
        val groupMembersViewModel = rememberScreenModel { GroupMembersViewModel(groupId) }
        val navigator = LocalNavigator.currentOrThrow
        GroupMembersLayout(
            groupMembersViewModel = groupMembersViewModel,
            navigator = navigator
        )
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

    val filteredUserInfos: List<UserInfo> = userInfos.filter { userInfo ->
        userInfo.user.displayName.contains(usernameSearchQuery, ignoreCase = true)
    }

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
            LazyColumn(
                contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
                modifier = Modifier.fillMaxSize().padding(padding)
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
                            .padding(AppTheme.dimensions.cardPadding)
                    )
                    Spacer(modifier = Modifier.height(AppTheme.dimensions.spacingSmall))
                }
                itemsIndexed(filteredUserInfos) { i, userInfo ->
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .run {
                                if (filteredUserInfos.size == 1) this.clip(AppTheme.shapes.large)
                                else if (i == 0) this.clip(AppTheme.shapes.topLargeShape)
                                else if (i == filteredUserInfos.size - 1)
                                    this.clip(AppTheme.shapes.bottomLargeShape)
                                else this
                            }
                            .clickable { userInfoOnClick(userInfo) }
                            .background(AppTheme.colors.secondary)
                            .padding(AppTheme.dimensions.cardPadding)
                    ) { UserInfoRowCard(userInfo = userInfo) }
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
                        Caption(text = "Joined on ${isoFullDate(joinDate)}")
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
    state: ModalBottomSheetState,
    addToGroupUsernameSearchQuery: String,
    onQueryChange: (String) -> Unit,
    inviteUsernameToGroupOnClick: () -> Unit,
    inviteResult: GroupMembersViewModel.InviteResult,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.appPadding)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    UsernameSearchBar(
                        usernameSearchQuery = addToGroupUsernameSearchQuery,
                        labelText = "Search by username",
                        onQueryChange = onQueryChange,
                        border = when(inviteResult) {
                            is GroupMembersViewModel.InviteResult.Error -> AppTheme.colors.error
                            else -> Color.Transparent
                        }
                    )
                    Caption(
                        text = when(inviteResult) {
                            is GroupMembersViewModel.InviteResult.Error ->
                                inviteResult.exception.message ?: ""
                            is GroupMembersViewModel.InviteResult.Sent -> "Sent!"
                            else -> ""
                        }
                    )
                }
                H1ConfirmTextButton(text = "Add to Group", onClick = inviteUsernameToGroupOnClick)
            }
        },
        content = content
    )
}
