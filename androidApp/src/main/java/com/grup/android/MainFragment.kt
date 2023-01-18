package com.grup.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.accompanist.pager.*
import com.grup.APIServer
import com.grup.android.ui.apptheme.*
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.UserInfo
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {

            setContent {
                MainLayout(
                    mainViewModel = mainViewModel,
                    navController = findNavController()
                )
            }.also {
                if (!mainViewModel.hasUserObject) {
                    findNavController().navigate(R.id.startWelcomeSlideshow)
                }
            }
        }
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class, ExperimentalPagerApi::class)
@Composable
fun MainLayout(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val addToGroupBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val groupNotificationsBottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val groups: List<Group> by mainViewModel.groupsList.collectAsStateWithLifecycle()
    val selectedGroup: Group? by mainViewModel.selectedGroup
    val groupInvites: List<GroupInvite> by mainViewModel.groupInvitesList.collectAsState()
    val userInfos: List<UserInfo> by mainViewModel.userInfos.collectAsStateWithLifecycle()

    fun openDrawer() = scope.launch { scaffoldState.drawerState.open() }
    fun closeDrawer() = scope.launch { scaffoldState.drawerState.close() }
    fun selectedGroupOnValueChange(group: Group) {
        mainViewModel.selectedGroup.value = group
    }

    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
        GroupNotificationsBottomSheet(
            groupInvites = groupInvites,
            state = groupNotificationsBottomSheetState,
            groupInviteOnClick = { groupInvite ->
                mainViewModel.acceptInviteToGroup(groupInvite)
                selectedGroupOnValueChange(groups.find { it.getId() == groupInvite.groupId }!!)
            }
        ) {
            selectedGroup?.let {
                AddToGroupBottomSheetLayout(
                    selectedGroup = it,
                    state = addToGroupBottomSheetState,
                    inviteUsernameToGroup = { username, group ->
                        mainViewModel.inviteUserToGroup(username, group)
                        closeDrawer()
                    },
                    content = content
                )
            } ?: content()
        }
    }

    modalSheets {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopBar(
                    onNavigationIconClick = { openDrawer() },
                    actions = {
                        if (selectedGroup != null) {
                            AddToGroupButton(
                                addToGroupOnClick = {
                                    scope.launch { addToGroupBottomSheetState.show() }
                                }
                            )
                        }
                        GroupNotificationsButton(
                            groupNotificationsOnClick = {
                                scope.launch { groupNotificationsBottomSheetState.show() }
                            }
                        )
                    }
                )
            },
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            drawerBackgroundColor = AppTheme.colors.secondary,
            drawerContent = {
                GroupNavigationMenu(
                    groups = groups,
                    onItemClick = { menuItem ->
                        selectedGroupOnValueChange(groups[menuItem.index])
                        closeDrawer()
                    },
                    createGroup = { groupName ->
                        selectedGroupOnValueChange(mainViewModel.createGroup(groupName))
                        closeDrawer()
                    }
                )
            },
            bottomBar = { /* TODO */ },
            backgroundColor = AppTheme.colors.primary,
            modifier = Modifier.fillMaxSize()
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(padding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colors.onPrimary
                ) {
                    selectedGroup?.let { group ->
                        GroupDetails(
                            group = group,
                            userInfos = userInfos,
                            moneyRequestOnClick = {
                                navController.navigate(R.id.debtActionFragment)
                            }
                        )
                        PublicRequestsDetails()
                    } ?: NoGroupsDisplay()
                }
            }
        }
    }
}

@Composable
fun NoGroupsDisplay() {
    Text(text = "Yaint in any groups bozo", color = AppTheme.colors.onPrimary)
}

@Composable
fun GroupNavigationMenu(
    groups: List<Group>,
    onItemClick: (GroupItem) -> Unit,
    createGroup: (String) -> Unit
) {
    DrawerHeader()
    DrawerBody(
        items = groups.mapIndexed { index, group ->
            GroupItem(
                id = group.getId(),
                index = index,
                groupName = group.groupName!!,
                contentDescription = "Open ${group.groupName}'s details",
                icon = Icons.Default.Home
            )
        },
        onItemClick = {
            onItemClick(it)
        }
    )
    Button(
        onClick = { createGroup("SAMPLE GROUP NAME") }
    ) {
        Text(text = "Create new group")
    }
    DrawerSettings(
        items = listOf(
            MenuItem(
                id = "home",
                title = "Create New Group",
                contentDescription = "Go to the home screen",
                icon = Icons.Default.AddCircle
            ),
            MenuItem(
                id = "home",
                title = "Settings",
                contentDescription = "Go to the settings screen",
                icon = Icons.Default.Settings
            ),
            MenuItem(
                id = "home",
                title = "Sign Out",
                contentDescription = "Go to the home screen",
                icon = Icons.Default.ExitToApp
            ),
        ),
        onItemClick = {
            println("Clicked on ${it.title}")
        }
    )
}

@Composable
fun TopBar(
    onNavigationIconClick: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = {},
        backgroundColor = AppTheme.colors.primary,
        navigationIcon = {
            IconButton(
                onClick = onNavigationIconClick
            ) {
                smallIcon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = actions
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun AddToGroupBottomSheetLayout(
    selectedGroup: Group,
    inviteUsernameToGroup: (String, Group) -> Unit,
    state: ModalBottomSheetState,
    backgroundColor: Color = AppTheme.colors.secondary,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    val username = remember { mutableStateOf(TextFieldValue()) }

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.paddingMedium)
            ) {
                TextField(
                    label = {
                        Text(
                            text = "Username to add",
                            color = textColor
                        )
                    },
                    textStyle = TextStyle(color = textColor),
                    value = username.value,
                    onValueChange = { username.value = it }
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { inviteUsernameToGroup(username.value.text, selectedGroup) }
                ) {
                    Text(text = "Add to group", color = textColor)
                }
            }
        },
        sheetBackgroundColor = backgroundColor,
        content = content
    )
}

@Composable
fun AddToGroupButton(
    addToGroupOnClick: () -> Unit
) {
    IconButton(onClick = addToGroupOnClick) {
        smallIcon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add to Group"
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun GroupNotificationsBottomSheet(
    groupInvites: List<GroupInvite>,
    groupInviteOnClick: (GroupInvite) -> Unit,
    state: ModalBottomSheetState,
    backgroundColor: Color = AppTheme.colors.secondary,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.paddingMedium)
            ) {
                Text(text = "NOTIFICATIONS LIST", color = textColor)
                Spacer(modifier = Modifier.height(8.dp))
                groupInvites.forEach { groupInvite ->
                    Text(
                        text = AnnotatedString(
                            "Group Invite to ${groupInvite.groupName!!}"
                        ),
                        color = textColor
                    )
                    Button(onClick = { groupInviteOnClick(groupInvite) }) {
                        Text(text = "Join ${groupInvite.groupName!!}")
                    }
                }
            }
        },
        sheetBackgroundColor = backgroundColor,
        content = content
    )
}

@Composable
fun GroupNotificationsButton(
    groupNotificationsOnClick: () -> Unit
) {
    IconButton(onClick = groupNotificationsOnClick) {
        smallIcon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications"
        )
    }
}

@Composable
fun GroupDetails(
    group: Group,
    userInfos: List<UserInfo>,
    moneyRequestOnClick: () -> Unit
) {
    userInfos.find { it.userId == APIServer.user.getId() }?.let { myUserInfo ->
        Column (
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .size(AppTheme.dimensions.groupDetailsSize)
                .padding(top = AppTheme.dimensions.paddingLarge)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimensions.paddingLarge)
            ) {
                Row (
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
                        text = "${group.groupName}",
                        modifier = Modifier.padding(top = AppTheme.dimensions.paddingLarge)
                    )
                }
                h1Text(
                    "$${myUserInfo.userBalance}",
                    fontSize = 100.sp
                )
            }
            TextButton(
                colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
                modifier = Modifier
                    .padding(bottom = AppTheme.dimensions.paddingMedium)
                    .width(250.dp)
                    .height(45.dp),
                shape = AppTheme.shapes.large,
                onClick = moneyRequestOnClick
            ) {
                Text(
                    text = "Money Request",
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    color = AppTheme.colors.onPrimary,
                )
            }
        }
    } ?: Text(text = "Loading group...", color = AppTheme.colors.onPrimary)

}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PublicRequestsDetails() {

    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val tabItems = listOf(
        "All",
        "Personal",
        "Settle"
    )

    val sampleList = mapOf(
        "4/20" to listOf("test1", "test2", "test3", "test4"),
        "6/9" to listOf("test5", "test6", "test7", "test8")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(AppTheme.shapes.large)
            .background(AppTheme.colors.secondary)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(top = AppTheme.dimensions.paddingMedium)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.smallSpacing)
                ) {

                    val indicator = @Composable { tabPositions: List<TabPosition> ->
                        CustomIndicator(tabPositions, pagerState)
                    }

                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        backgroundColor = AppTheme.colors.primary,
                        indicator = indicator,
                        modifier = Modifier
                            .padding(all = 5.dp)
                            .clip(AppTheme.shapes.medium)
                            .height(30.dp),
                    ) {
                        tabItems.forEachIndexed {index, title ->
                            Tab(
                                text = {
                                    Text(
                                        title,
                                        style = TextStyle(
                                            color = AppTheme.colors.onPrimary,
                                            fontSize = 16.sp)
                                    )
                                },
                                modifier = Modifier
                                    .zIndex(6f),
                                selected = pagerState.currentPage == index,
                                onClick = {
                                    scope.launch {
                                        pagerState.animateScrollToPage(index)
                                    }
                                }
                            )
                        }
                    }

                }
            }
            // contains the lists that are scrolled through
            HorizontalPager(
                count = 3,
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
            ) { page ->

                when (page) {
                    0 -> {
                        PublicRequestsList(content = sampleList)
                    }

                    1 -> {
                        PublicRequestsList(content = sampleList)
                    }

                    2 -> {
                        PublicRequestsList(content = sampleList)
                    }
                }
            }
        }
    }
}

@Composable
fun PublicRequestsList(content: Map<String, List<String>>) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(content.keys.toList()) { _: Int, filterGroup: String ->
            caption(
                text = "Completed - $filterGroup",
                modifier = Modifier.padding(start = AppTheme.dimensions.paddingExtraLarge)
            )
            Spacer(modifier = Modifier.height(AppTheme.dimensions.spacing / 2))
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                content[filterGroup]!!.forEach { request ->
                    h1Text(
                        text = request,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CustomIndicator(tabPositions: List<TabPosition>, pagerState: PagerState) {
    val transition = updateTransition(pagerState.currentPage)
    val indicatorStart by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 50f)
            } else {
                spring(dampingRatio = 1f, stiffness = 1000f)
            }
        }, label = ""
    ) {
        tabPositions[it].left
    }

    val indicatorEnd by transition.animateDp(
        transitionSpec = {
            if (initialState < targetState) {
                spring(dampingRatio = 1f, stiffness = 1000f)
            } else {
                spring(dampingRatio = 1f, stiffness = 50f)
            }
        }, label = ""
    ) {
        tabPositions[it].right
    }

    Box(
        Modifier
            .offset(x = indicatorStart)
            .wrapContentSize(align = Alignment.BottomStart)
            .width(indicatorEnd - indicatorStart)
            .padding(2.dp)
            .fillMaxSize()
            .background(color = AppTheme.colors.caption, AppTheme.shapes.medium)
            .zIndex(1f)
    )
}