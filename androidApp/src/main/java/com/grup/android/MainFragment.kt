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
import androidx.compose.foundation.lazy.itemsIndexed
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
import com.grup.models.*
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

@OptIn(ExperimentalLifecycleComposeApi::class, ExperimentalMaterialApi::class)
@Composable
fun MainLayout(
    mainViewModel: MainViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val actionDetailsBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    val groups: List<Group> by mainViewModel.groups.collectAsStateWithLifecycle()
    val selectedGroup: Group? by mainViewModel.selectedGroup.collectAsStateWithLifecycle()
    val myUserInfo: UserInfo? by mainViewModel.myUserInfo.collectAsStateWithLifecycle()
    val groupActivity: List<TransactionActivity> by
            mainViewModel.groupActivity.collectAsStateWithLifecycle()
    val activeSettleActions: List<SettleAction> by
            mainViewModel.activeSettleActions.collectAsStateWithLifecycle()

    var selectedAction: Action? by remember { mutableStateOf(null) }

    val openDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.open() } }
    val closeDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.close() } }
    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
        selectedAction?.let { selectedAction ->
            ActionDetailsBottomSheet(
                action = selectedAction,
                state = actionDetailsBottomSheetState,
                navigateSettleActionTransactionOnClick = {
                    navController.navigate(
                        R.id.actionAmountFragment,
                        Bundle().apply {
                            this.putString("actionType", TransactionViewModel.SETTLE_TRANSACTION)
                            this.putString("actionId", selectedAction.getId())
                        }
                    )
                },
                onBackPress = { scope.launch { actionDetailsBottomSheetState.hide() } },
                content = content
            )
        } ?: content()
    }

    modalSheets {
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
                    onGroupClick = { index ->
                        mainViewModel.onSelectedGroupChange(groups[index])
                        closeDrawer()
                    },
                    isSelectedGroup = { it.getId() == selectedGroup?.getId() },
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
                            activeSettleActions = activeSettleActions,
                            settleActionCardOnClick = { settleAction ->
                                selectedAction = settleAction
                                scope.launch { actionDetailsBottomSheetState.show() }
                            }
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
}

@Composable
fun NoGroupsDisplay() {
    Text(text = "Yaint in any groups bozo", color = AppTheme.colors.onPrimary)
}

@Composable
fun GroupNavigationMenu(
    groups: List<Group>,
    onGroupClick: (Int) -> Unit,
    isSelectedGroup: (Group) -> Boolean,
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
        LazyColumn(verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing)) {
            itemsIndexed(groups) { index, group ->
                GroupNavigationRow(
                    group = group,
                    onGroupClick = { onGroupClick(index) },
                    isSelected = isSelectedGroup(group)
                )
            }
        }
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            Divider(color = AppTheme.colors.primary, thickness = 3.dp)
            DrawerSettings(
                items = listOf(
                    MenuItem(
                        id = "new_group",
                        title = "Create New Group",
                        contentDescription = "Create a new group",
                        icon = Icons.Default.AddCircle,
                        onClick = { navigateCreateGroupOnClick() }
                    ),
                    MenuItem(
                        id = "settings",
                        title = "Settings",
                        contentDescription = "Open settings screen",
                        icon = Icons.Default.Settings,
                        onClick = { }
                    ),
                    MenuItem(
                        id = "logout",
                        title = "Sign Out",
                        contentDescription = "Log out",
                        icon = Icons.Default.ExitToApp,
                        onClick = {
                            logOutOnClick()
                            context.startActivity(
                                Intent(context, LoginActivity::class.java)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            )
                        }
                    ),
                )
            )
        }
    }
}

@Composable
fun GroupNavigationRow(
    group: Group,
    onGroupClick: () -> Unit,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.dimensions.appPadding)
            .clip(AppTheme.shapes.medium)
            .clickable { onGroupClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.dimensions.paddingSmall)
        ) {
            Box(
                modifier =
                    if (isSelected)
                        Modifier.border(
                            width = 2.dp,
                            color = Color.White
                        )
                    else
                        Modifier
            ) {
                SmallIcon(
                    imageVector = Icons.Default.Home,
                    contentDescription = group.groupName!!
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            H1Text(
                text = group.groupName!!,
                color = AppTheme.colors.onPrimary,
                modifier = Modifier.weight(1f)
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
        title = { group?.let { H1Text(text = it.groupName!!) } },
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
            H1Text(text = "Your Balance")
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
                    H1Text(
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
                    H1Text(
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
    activeSettleActions: List<SettleAction>,
    settleActionCardOnClick: (SettleAction) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = modifier
    ) {
        H1Text(text = "Requests", fontWeight = FontWeight.Medium)
        if (activeSettleActions.isNotEmpty()) {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                items(activeSettleActions) { settleAction ->
                    SettleActionCard(
                        settleAction = settleAction,
                        settleActionCardOnClick = { settleActionCardOnClick(settleAction) }
                    )
                }
            }
        } else {
            // TODO: No requests display
        }
    }
}

@Composable
fun SettleActionCard(
    settleAction: SettleAction,
    settleActionCardOnClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(AppTheme.shapes.large)
            .width(140.dp)
            .height(140.dp)
            .background(AppTheme.colors.secondary)
            .clickable(onClick = settleActionCardOnClick)
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ActionDetailsBottomSheet(
    action: Action,
    state: ModalBottomSheetState,
    onBackPress: () -> Unit,
    navigateSettleActionTransactionOnClick: () -> Unit,
    background: Color = AppTheme.colors.primary,
    content: @Composable () -> Unit
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            when(action) {
                is DebtAction -> TODO()
                is SettleAction -> SettleActionDetails(
                    settleAction = action,
                    navigateSettleActionTransactionOnClick = navigateSettleActionTransactionOnClick,
                    onBackPress = onBackPress,
                    background = background
                )
            }
        },
        sheetBackgroundColor = background,
        content = content
    )
}

@Composable
fun SettleActionDetails(
    settleAction: SettleAction,
    navigateSettleActionTransactionOnClick: () -> Unit,
    onBackPress: () -> Unit,
    background: Color = AppTheme.colors.primary
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        SmallIcon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                backgroundColor = background
            )
        },
        backgroundColor = background,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.dimensions.appPadding)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ProfileIcon(
                    imageVector = Icons.Default.Face,
                    iconSize = 90.dp
                )
                Column(horizontalAlignment = Alignment.Start) {
                    Caption(text = "Remaining Amount")
                    MoneyAmount(
                        moneyAmount = settleAction.remainingAmount,
                        fontSize = 48.sp
                    )
                }
            }
            LazyColumn() {

            }
            Spacer(modifier = Modifier.weight(1f))
            H1ConfirmTextButton(
                text = "Settle",
                onClick = navigateSettleActionTransactionOnClick
            )
        }
    }
}
