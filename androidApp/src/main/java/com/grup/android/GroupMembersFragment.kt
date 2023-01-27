package com.grup.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.ui.apptheme.AppTheme
import com.grup.android.ui.caption
import com.grup.android.ui.h1Text
import com.grup.android.ui.SmallIcon
import com.grup.models.UserInfo
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
    
    var usernameSearchQuery: String by remember { mutableStateOf("") }

    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
        AddToGroupBottomSheetLayout(
            state = addToGroupBottomSheetState,
            inviteUsernameToGroupOnClick = { username ->
                groupMembersViewModel.inviteUserToGroup(username)
                scope.launch {
                    addToGroupBottomSheetState.hide()
                }
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
                    title = { Text("Members", color = AppTheme.colors.onSecondary) },
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
                            addToGroupOnClick = {
                                scope.launch { addToGroupBottomSheetState.show() }
                            }
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
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        UsernameSearchBar(
                            usernameSearchQuery = usernameSearchQuery,
                            onUsernameChange = { usernameSearchQuery = it }
                        )
                    }
                    UsersList(
                        userInfos = userInfos.filter { userInfo ->
                            userInfo.nickname!!.contains(usernameSearchQuery, ignoreCase = true)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun UsernameSearchBar(
    usernameSearchQuery: String,
    onUsernameChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        TextField(
            value = usernameSearchQuery,
            onValueChange = onUsernameChange,
            label = { Text("Search", color = AppTheme.colors.primary) },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "SearchIcon"
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppTheme.shapes.large)
                .background(AppTheme.colors.secondary)
                .padding(all = AppTheme.dimensions.paddingMedium),
            colors = TextFieldDefaults.textFieldColors(
                textColor = AppTheme.colors.primary,
                disabledTextColor = Color.Transparent,
                backgroundColor = AppTheme.colors.onPrimary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}

@Composable
fun UsersList(
    userInfos: List<UserInfo>
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.smallSpacing),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(userInfos) { _, userInfo ->
            UserDisplay(userInfo = userInfo)
        }
    }
}

@Composable
fun UserDisplay(
    userInfo: UserInfo
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.dimensions.paddingMedium)
    ) {
        Row {
            Icon(
                imageVector = Icons.Default.Face,
                contentDescription = "Profile Picture",
                modifier = Modifier
                    .size(70.dp)
                    .padding(horizontal = AppTheme.dimensions.paddingSmall)
            )
            Column(verticalArrangement = Arrangement.Center) {
                h1Text(text = userInfo.nickname!!)
                caption(text = "This is a description")
            }
        }
        Text(text = "$${userInfo.userBalance}")
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupMemberInfoBottomSheet(
    state: ModalBottomSheetState,
    backgroundColor: Color = AppTheme.colors.secondary,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = AppTheme.dimensions.paddingLarge)
                ) {
                    Icon(
                        imageVector = Icons.Default.Face,
                        contentDescription = "Profile Picture",
                        modifier = Modifier.size(98.dp)
                    )
                    h1Text(
                        text = "Member",
                        modifier = Modifier.padding(top = AppTheme.dimensions.paddingLarge),
                        color = textColor
                    )
                }
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
    inviteUsernameToGroupOnClick: (String) -> Unit,
    state: ModalBottomSheetState,
    backgroundColor: Color = AppTheme.colors.secondary,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    var username: String by remember { mutableStateOf("") }

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
                    usernameSearchQuery = username,
                    onUsernameChange = { username = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { inviteUsernameToGroupOnClick(username) },
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
                    shape = AppTheme.shapes.CircleShape
                ) {
                    Text(text = "Add to group", color = textColor)
                }
            }
        },
        sheetBackgroundColor = backgroundColor,
        content = content
    )
}