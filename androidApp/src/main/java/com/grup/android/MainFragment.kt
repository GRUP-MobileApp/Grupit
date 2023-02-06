package com.grup.android

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.login.LoginActivity
import com.grup.android.transaction.TransactionActivity
import com.grup.android.transaction.TransactionViewModel
import com.grup.android.ui.*
import com.grup.android.ui.apptheme.*
import com.grup.models.Group
import com.grup.models.SettleAction
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
                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colors.onPrimary
                ) {
                    MainLayout(
                        mainViewModel = mainViewModel,
                        navController = findNavController()
                    )
                }
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
    val activeSettleActions: List<SettleAction> by
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
                group = selectedGroup,
                onNavigationIconClick = openDrawer,
                navigateGroupMembersOnClick = { navController.navigate(R.id.viewMembers) }
            )
        },
        drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
        drawerBackgroundColor = AppTheme.colors.secondary,
        drawerContent = {
            //delete later
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
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(padding)
                .padding(top = AppTheme.dimensions.appPadding)
        ) {
            if (groups.isNotEmpty()) {
                myUserInfo?.let { myUserInfo ->
                    GroupBalanceCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.dimensions.appPadding),
                        myUserInfo = myUserInfo,
                        navigateDebtActionAmountOnClick = {
                            navController.navigate(
                                R.id.enterActionAmount,
                                Bundle().apply {
                                    this.putString("actionType", TransactionViewModel.DEBT)
                                }
                            )
                        },
                        navigateSettleActionAmountOnClick = {
                            navController.navigate(
                                R.id.enterActionAmount,
                                Bundle().apply {
                                    this.putString("actionType", TransactionViewModel.SETTLE)
                                }
                            )
                        }
                    )
                    ActiveSettleActions(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.dimensions.appPadding)
                            .padding(top = AppTheme.dimensions.spacingLarge),
                        activeSettleActions = activeSettleActions
                    )
                    RecentActivityList(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.dimensions.appPadding)
                            .padding(top = AppTheme.dimensions.spacingLarge),
                        groupActivity = groupActivity
                    )
                }
            } else {
                NoGroupsDisplay()
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
    logOutOnClick: () -> Unit,
    background: Color = AppTheme.colors.primary
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
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
}

@Composable
fun TopBar(
    group: Group?,
    onNavigationIconClick: () -> Unit,
    navigateGroupMembersOnClick: () -> Unit
) {
    TopAppBar(
        title = { group?.let { h1Text(text = it.groupName!!) } },
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
        actions = {
            if (group != null) {
                MembersButton(navigateGroupMembersOnClick = navigateGroupMembersOnClick)
            }
        }
    )
}

@Composable
fun TestDebtButton(
    debtOnClick: () -> Unit
) {
    IconButton(
        onClick = debtOnClick
    ) {
        SmallIcon(
            imageVector = Icons.Default.Home,
            contentDescription = "Members"
        )
    }
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
    navigateGroupMembersOnClick: () -> Unit
) {
    IconButton(
        onClick = navigateGroupMembersOnClick
    ) {
        SmallIcon(
            imageVector = Icons.Default.Person,
            contentDescription = "Members"
        )
    }
}

@Composable
fun GroupBalanceCard(
    modifier: Modifier = Modifier,
    myUserInfo: UserInfo,
    navigateDebtActionAmountOnClick: () -> Unit,
    navigateSettleActionAmountOnClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(AppTheme.shapes.extraLarge)
            .background(AppTheme.colors.secondary)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingSmall),
            modifier = Modifier
                .fillMaxWidth()
                .padding(AppTheme.dimensions.cardPadding)
        ) {
            h1Text(text = "Your Balance")
            MoneyAmount(moneyAmount = myUserInfo.userBalance)
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimensions.spacing)
            ) {
                TextButton(
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
                    modifier = Modifier
                        .width(140.dp)
                        .height(45.dp),
                    shape = AppTheme.shapes.CircleShape,
                    onClick = navigateDebtActionAmountOnClick
                ) {
                    h1Text(
                        text = "Request",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = AppTheme.colors.onPrimary,
                    )
                }
                TextButton(
                    colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
                    modifier = Modifier
                        .width(140.dp)
                        .height(45.dp),
                    shape = AppTheme.shapes.CircleShape,
                    onClick = navigateSettleActionAmountOnClick
                ) {
                    h1Text(
                        text = "Settle",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = AppTheme.colors.onPrimary,
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveSettleActions(
    modifier: Modifier = Modifier,
    activeSettleActions: List<SettleAction>
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = modifier
    ) {
        h1Text(text = "Requests")
        if (activeSettleActions.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(activeSettleActions) { settleAction ->
                    SettleActionCard(settleAction = settleAction)
                }
            }
        }
    }
}

@Composable
fun SettleActionCard(
    settleAction: SettleAction
) {
    Box(
        modifier = Modifier
            .clip(AppTheme.shapes.large)
            .width(140.dp)
            .height(140.dp)
            .background(AppTheme.colors.secondary)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            modifier = Modifier
                .padding(AppTheme.dimensions.cardPadding)
        ) {
            ProfileIcon(
                imageVector = Icons.Default.Face,
                iconSize = 50.dp
            )
            MoneyAmount(
                moneyAmount = settleAction.settleAmount!!,
                fontSize = 24.sp
            )
        }
    }
}

@Composable
fun RecentActivityList(
    modifier: Modifier = Modifier,
    groupActivity: List<TransactionActivity>
) {
    val groupActivityByDate: Map<String, List<TransactionActivity>> =
        groupActivity.groupBy { isoDate(it.date) }
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = modifier
    ) {
        h1Text(text = "Recent Transactions")
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(AppTheme.shapes.large)
                .background(AppTheme.colors.secondary)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(AppTheme.dimensions.cardPadding)
            ) {
                items(groupActivityByDate.keys.toList()) { date ->
                    caption(text = date)
                    Spacer(modifier = Modifier.height(AppTheme.dimensions.spacing))
                    Column(
                        verticalArrangement = Arrangement
                            .spacedBy(AppTheme.dimensions.spacingSmall),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        groupActivityByDate[date]!!.forEach { transactionActivity ->  
                            h1Text(
                                text = transactionActivity.displayText(),
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
