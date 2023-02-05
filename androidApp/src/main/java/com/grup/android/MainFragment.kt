package com.grup.android

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.google.accompanist.pager.*
import com.grup.android.login.LoginActivity
import com.grup.android.transaction.TransactionActivity
import com.grup.android.ui.*
import com.grup.android.ui.apptheme.*
import com.grup.models.Group
import com.grup.models.UserInfo
import kotlinx.coroutines.launch

class MainFragment : Fragment() {
    private val mainViewModel: MainViewModel by navGraphViewModels(R.id.main_graph)

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().onBackPressedDispatcher
    }

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
            }
        }
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun MainLayout(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val groups: List<Group> by mainViewModel.groups.collectAsStateWithLifecycle()
    val selectedGroup: Group? by mainViewModel.selectedGroup.collectAsStateWithLifecycle()
    val myUserInfo: UserInfo? by mainViewModel.myUserInfo.collectAsStateWithLifecycle()
    val groupActivity: List<TransactionActivity> by
            mainViewModel.groupActivity.collectAsStateWithLifecycle()
    val activeSettleActions: List<TransactionActivity.CreateSettleAction> by
            mainViewModel.activeSettleActions.collectAsStateWithLifecycle()

    val openDrawer: () -> Unit = {
        scope.launch { scaffoldState.drawerState.open() }
    }
    val closeDrawer: () -> Unit = {
        scope.launch { scaffoldState.drawerState.close() }
    }
    fun selectedGroupOnValueChange(group: Group) = mainViewModel.onSelectedGroupChange(group)

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopBar(
                onNavigationIconClick = openDrawer,
                actions = {
                    if (selectedGroup!= null) {
                        MembersButton(
                            membersOnClick = {
                                navController.navigate(R.id.viewMembers)
                            }
                        )
                    }
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
                navigateNotificationsOnClick = {
                    closeDrawer()
                    navController.navigate(R.id.openNotifications)
                },
                navigateCreateGroupOnClick = {
                    closeDrawer()
                    navController.navigate(R.id.createGroup)
                },
                logOutOnClick = { mainViewModel.logOut() }
            )
        },
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
                if (groups.isNotEmpty()) {
                    myUserInfo?.let { myUserInfo ->
                        GroupDetails(
                            myUserInfo = myUserInfo,
                            group = selectedGroup ?: selectedGroupOnValueChange(groups[0]),
                            navigateActionAmountOnClick = {
                                navController.navigate(R.id.enterActionAmount)
                            }
                        )
                        RecentActivityList(
                            groupActivity = groupActivity,
                            activeSettleActions = activeSettleActions
                        )
                    }
                } else {
                    NoGroupsDisplay()
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
    navigateNotificationsOnClick: () -> Unit,
    navigateCreateGroupOnClick: () -> Unit,
    logOutOnClick: () -> Unit
) {
    val context = LocalContext.current

    DrawerHeader(navigateNotificationsOnClick = navigateNotificationsOnClick)
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

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        Divider(color = AppTheme.colors.primary, thickness = 3.dp)
        DrawerSettings(
            items = listOf(
                MenuItem(
                    id = "home",
                    title = "Create New Group",
                    contentDescription = "Go to the home screen",
                    icon = Icons.Default.AddCircle,
                    onClick = { navigateCreateGroupOnClick() }
                ),
                MenuItem(
                    id = "home",
                    title = "Settings",
                    contentDescription = "Go to the settings screen",
                    icon = Icons.Default.Settings,
                    onClick = {}
                ),
                MenuItem(
                    id = "home",
                    title = "Sign Out",
                    contentDescription = "Go to the home screen",
                    icon = Icons.Default.ExitToApp,
                    onClick = {
                        logOutOnClick()
                        context.startActivity(
                            Intent(context, LoginActivity::class.java)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        )
                    }
                ),
            ),
            onItemClick = {
                it.onClick()
                println("Clicked on ${it.title}")
            }
        )
    }
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
                SmallIcon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Menu"
                )
            }
        },
        actions = actions
    )
}

@Composable
fun NotificationsButton(
    navigateNotificationsOnClick: () -> Unit
) {
    IconButton(onClick = navigateNotificationsOnClick) {
        SmallIcon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notifications"
        )
    }
}

@Composable
fun MembersButton(
    membersOnClick: () -> Unit
) {
    IconButton(
        onClick = membersOnClick
    ) {
        SmallIcon(
            imageVector = Icons.Default.Person,
            contentDescription = "Members"
        )
    }
}

@Composable
fun GroupDetails(
    myUserInfo: UserInfo,
    group: Group,
    navigateActionAmountOnClick: () -> Unit
) {
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
            shape = AppTheme.shapes.CircleShape,
            onClick = navigateActionAmountOnClick
        ) {
            Text(
                text = "Money Request",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = AppTheme.colors.onPrimary,
            )
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun RecentActivityList(
    groupActivity: List<TransactionActivity>,
    activeSettleActions: List<TransactionActivity.CreateSettleAction>
) {
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    val tabItems = listOf(
        "All",
        "Settle"
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
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall)
                ) {
                    val indicator: @Composable (List<TabPosition>) -> Unit = { tabPositions ->
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
                    0 -> RecentGroupActivityList(groupActivity = groupActivity)
                    1 -> PublicRequestsList(activeSettleActions = activeSettleActions)
                }
            }
        }
    }
}

@Composable
fun RecentGroupActivityList(
    groupActivity: List<TransactionActivity>
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(groupActivity) { _, transactionActivity ->
            Text(text = transactionActivity.displayText())
        }
    }
}

@Composable
fun PublicRequestsList(
    activeSettleActions: List<TransactionActivity.CreateSettleAction>
) {
    LazyColumn(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = Modifier.fillMaxSize()
    ) {
        itemsIndexed(activeSettleActions) { _, createSettleAction ->
            Text(text = createSettleAction.displayText())
        }
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun CustomIndicator(tabPositions: List<TabPosition>, pagerState: PagerState) {
    val transition = updateTransition(pagerState.currentPage, label = "")
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