package com.grup.ui.compose.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.*
import com.grup.ui.compose.asMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.compose.profilePicturePainter
import com.grup.ui.*
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.compose.BackPressModalBottomSheetLayout
import com.grup.ui.compose.Caption
import com.grup.ui.compose.DrawerSettings
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.MenuItem
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.ProfileIcon
import com.grup.ui.compose.SmallIcon
import com.grup.ui.compose.UserInfoRowCard
import com.grup.ui.compose.recentActivityList
import com.grup.ui.models.TransactionActivity
import com.grup.ui.viewmodel.GroupInvitesViewModel
import com.grup.ui.viewmodel.MainViewModel
import com.grup.ui.viewmodel.NotificationsViewModel
import com.grup.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

internal class MainView : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val mainViewModel: MainViewModel = rememberScreenModel { MainViewModel() }
        val notificationsViewModel: NotificationsViewModel =
            rememberScreenModel { NotificationsViewModel() }
        val groupInvitesViewModel: GroupInvitesViewModel =
            rememberScreenModel { GroupInvitesViewModel() }

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            MainLayout(
                mainViewModel = mainViewModel,
                notificationsViewModel = notificationsViewModel,
                groupInvitesViewModel = groupInvitesViewModel,
                navigator = navigator,
                logOutAuthProviderOnClick = {
                    mainViewModel.logOut()
                    navigator.popUntil { screen ->
                        screen is ReleaseLoginView || screen is DebugLoginView
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun MainLayout(
    mainViewModel: MainViewModel,
    notificationsViewModel: NotificationsViewModel,
    groupInvitesViewModel: GroupInvitesViewModel,
    navigator: Navigator,
    logOutAuthProviderOnClick: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val actionDetailsBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val lazyListState = rememberLazyListState()

    val groups: List<Group> by mainViewModel.groups.collectAsStateWithLifecycle()
    val selectedGroup: Group? by mainViewModel.selectedGroup.collectAsStateWithLifecycle()
    val myUserInfo: UserInfo? by mainViewModel.myUserInfo.collectAsStateWithLifecycle()
    val groupActivity: List<TransactionActivity> by
        mainViewModel.groupActivity.collectAsStateWithLifecycle()
    val activeSettleActions: List<SettleAction> by
        mainViewModel.activeSettleActions.collectAsStateWithLifecycle()

    val groupInvitesAmount: Int by
        groupInvitesViewModel.groupInvitesCount.collectAsStateWithLifecycle()
    val notificationsAmount: Map<String, Int> by
        notificationsViewModel.notificationsCount.collectAsStateWithLifecycle()

    // Not getting updated
    var selectedAction: Action? by remember { mutableStateOf(null) }

    val openDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.open() } }
    val closeDrawer: () -> Unit = { scope.launch { scaffoldState.drawerState.close() } }
    val selectAction: (Action) -> Unit = { action ->
        selectedAction = action
        scope.launch { actionDetailsBottomSheetState.show() }
    }
    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
        selectedAction?.let { selectedAction ->
            BackPressModalBottomSheetLayout(
                sheetState = actionDetailsBottomSheetState,
                sheetContent = {
                    Scaffold(
                        topBar = {
                            TopAppBar(
                                title = {},
                                navigationIcon = {
                                    IconButton(
                                        onClick = {
                                            scope.launch {
                                                actionDetailsBottomSheetState.hide()
                                            }
                                        }
                                    ) {
                                        SmallIcon(
                                            imageVector = Icons.Filled.ArrowBack,
                                            contentDescription = "Back"
                                        )
                                    }
                                },
                                backgroundColor = AppTheme.colors.primary
                            )
                        },
                        backgroundColor = AppTheme.colors.primary,
                        modifier = Modifier
                            .fillMaxSize()
                    ) { padding ->
                        myUserInfo?.let { userInfo ->
                            when (selectedAction) {
                                is DebtAction -> DebtActionDetails(
                                    debtAction = selectedAction,
                                    modifier = Modifier
                                        .padding(padding)
                                        .padding(AppTheme.dimensions.appPadding)
                                )
                                is SettleAction -> SettleActionDetails(
                                    settleAction = selectedAction,
                                    myUserInfo = userInfo,
                                    navigateSettleActionTransactionOnClick = {
                                        if (userInfo.userBalance < 0) {
                                            navigator.push(
                                                ActionAmountScreen(
                                                    actionType =
                                                        TransactionViewModel.SETTLE_TRANSACTION,
                                                    existingActionId =
                                                        selectedAction.getId()
                                                )
                                            )
                                        }
                                    },
                                    acceptSettleActionTransactionOnClick = { transactionRecord ->
                                        mainViewModel.acceptSettleActionTransaction(
                                            selectedAction,
                                            transactionRecord
                                        )
                                        scope.launch { actionDetailsBottomSheetState.hide() }
                                    },
                                    modifier = Modifier
                                        .padding(padding)
                                        .padding(AppTheme.dimensions.appPadding)
                                )
                            }
                        }
                    }
                },
                content = content
            )
        } ?: content()
    }

    modalSheets {
        Scaffold(
            scaffoldState = scaffoldState,
            topBar = {
                TopAppBar(
                    title = { selectedGroup?.let {
                        H1Text(
                            text = it.groupName!!
                        )
                    } },
                    backgroundColor = AppTheme.colors.primary,
                    navigationIcon = {
                        BadgedBox(
                            badge = {
                                (notificationsAmount[selectedGroup?.getId()]?: 0)
                                    .let { selectedGroupNotificationsAmount ->
                                        if (
                                            notificationsAmount.values.sum() -
                                            selectedGroupNotificationsAmount +
                                            groupInvitesAmount > 0
                                        ) {
                                            Badge(
                                                backgroundColor = AppTheme.colors.error,
                                                modifier = Modifier
                                                    .offset((-8).dp, (10).dp)
                                            )
                                        }
                                    }
                            }
                        ) {
                            IconButton(onClick = openDrawer) {
                                SmallIcon(
                                    imageVector = Icons.Filled.Menu,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    },
                    actions = {
                        selectedGroup?.let { group ->
                            Row(
                                horizontalArrangement = Arrangement
                                    .spacedBy(AppTheme.dimensions.spacingSmall)
                            ) {
                                GroupNotificationsButton(
                                    groupNotificationsAmount =
                                    notificationsAmount[group.getId()] ?: 0,
                                    navigateGroupNotificationsOnClick = {
                                        navigator.push(GroupNotificationsView())
                                    }
                                )
                                MembersButton(
                                    navigateGroupMembersOnClick = {
                                        navigator.push(GroupMembersView())
                                    }
                                )
                            }
                        }
                    }
                )
            },
            drawerGesturesEnabled = scaffoldState.drawerState.isOpen,
            drawerBackgroundColor = AppTheme.colors.secondary,
            drawerContent = {
                //delete later
                GroupNavigationMenu(
                    groups = groups,
                    onGroupClick = { index ->
                        closeDrawer()
                        mainViewModel.onSelectedGroupChange(groups[index])
                    },
                    groupInvitesAmount = groupInvitesAmount,
                    notificationsAmount = notificationsAmount,
                    isSelectedGroup = { it.getId() == selectedGroup?.getId() },
                    navigateGroupInvitesOnClick = {
                        navigator.push(GroupInvitesView())
                    },
                    navigateCreateGroupOnClick = {
                        closeDrawer()
                        navigator.push(
                            CreateGroupView(
                                createGroupOnClick = { groupName ->
                                    mainViewModel.createGroup(
                                        groupName = groupName,
                                        onSuccess = { group ->
                                            mainViewModel.onSelectedGroupChange(group)
                                        },
                                        onFailure = { }
                                    )
                                }
                            )
                        )
                    },
                    navigateAccountSettingsOnClick = {
                        navigator.push(AccountSettingsView())
                    },
                    logOutOnClick = {
                        logOutAuthProviderOnClick()
                    }
                )
            },
            backgroundColor = AppTheme.colors.primary,
            modifier = Modifier
                .fillMaxSize()
        ) { padding ->
            LazyColumn(
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
                modifier = Modifier
                    .padding(padding)
            ) {
                if (groups.isNotEmpty()) {
                    myUserInfo?.let { myUserInfo ->
                        item {
                            GroupBalanceCard(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                myUserInfo = myUserInfo,
                                navigateDebtActionAmountOnClick = {
                                    navigator.push(
                                        ActionAmountScreen(actionType = TransactionViewModel.DEBT)
                                    )
                                },
                                navigateSettleActionAmountOnClick = {
                                    if (myUserInfo.userBalance > 0) {
                                        navigator.push(
                                            ActionAmountScreen(
                                                actionType = TransactionViewModel.SETTLE
                                            )
                                        )
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.height(AppTheme.dimensions.spacingLarge))
                        }
                        if (activeSettleActions.isNotEmpty()) {
                            item {
                                ActiveSettleActions(
                                    activeSettleActions = activeSettleActions,
                                    isMySettleAction = { settleAction ->
                                        settleAction.debteeUserInfo!!.userId!! ==
                                                myUserInfo.userId!!
                                    },
                                    settleActionCardOnClick = selectAction,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(AppTheme.dimensions.spacingLarge))
                            }
                        }
                        recentActivityList(
                            groupActivity = groupActivity,
                            transactionActivityOnClick = { transactionActivity ->
                                selectAction(transactionActivity.action)
                            }
                        )
                    }
                } else {
                    item { NoGroupsDisplay() }
                }
            }
        }
    }
}

@Composable
private fun NoGroupsDisplay() {
    Text(text = " ^ Create or join a group", color = AppTheme.colors.onSecondary)
}

@Composable
private fun GroupNavigationMenu(
    groups: List<Group>,
    onGroupClick: (Int) -> Unit,
    isSelectedGroup: (Group) -> Boolean,
    groupInvitesAmount: Int,
    notificationsAmount: Map<String, Int>,
    navigateGroupInvitesOnClick: () -> Unit,
    navigateCreateGroupOnClick: () -> Unit,
    navigateAccountSettingsOnClick: () -> Unit,
    logOutOnClick: () -> Unit,
    background: Color = AppTheme.colors.primary
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 20.dp)
        ) {
            Text(text = "Groups", fontSize = 40.sp, color = AppTheme.colors.onSecondary)
            Spacer(modifier = Modifier.weight(1f))
            BadgedBox(
                badge = {
                    if (groupInvitesAmount > 0) {
                        Badge(
                            backgroundColor = AppTheme.colors.error,
                            modifier = Modifier
                                .offset((-8).dp, (10).dp)
                                .clip(AppTheme.shapes.circleShape)
                                .align(Alignment.Center)
                        ) {
                            H1Text(
                                text = groupInvitesAmount.toString(),
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            ) {
                IconButton(onClick = navigateGroupInvitesOnClick) {
                    SmallIcon(
                        imageVector = Icons.Default.MailOutline,
                        contentDescription = "Notifications"
                    )
                }
            }
        }
        LazyColumn(verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing)) {
            itemsIndexed(groups) { index, group ->
                GroupNavigationRow(
                    group = group,
                    onGroupClick = { onGroupClick(index) },
                    groupNotificationsAmount = notificationsAmount[group.getId()] ?: 0,
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
                        id = "account",
                        title = "Account",
                        contentDescription = "Open account screen",
                        icon = Icons.Default.AccountCircle,
                        onClick = { navigateAccountSettingsOnClick() }
                    ),
                    MenuItem(
                        id = "logout",
                        title = "Sign Out",
                        contentDescription = "Log out",
                        icon = Icons.Default.ExitToApp,
                        onClick = logOutOnClick
                    ),
                )
            )
        }
    }
}

@Composable
private fun GroupNavigationRow(
    group: Group,
    onGroupClick: () -> Unit,
    groupNotificationsAmount: Int,
    isSelected: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AppTheme.dimensions.appPadding)
            .clip(AppTheme.shapes.medium)
            .clickable(onClick = onGroupClick)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = AppTheme.dimensions.paddingSmall)
        ) {
            BadgedBox(
                badge = {
                    if (!isSelected && groupNotificationsAmount > 0) {
                        Badge(
                            backgroundColor = AppTheme.colors.error,
                            modifier = Modifier
                                .offset((-6).dp, (8).dp)
                                .size(16.dp)
                                .clip(AppTheme.shapes.circleShape)
                                .align(Alignment.Center)
                        ) {
                            H1Text(
                                text = groupNotificationsAmount.toString(),
                                fontSize = 12.sp
                            )
                        }
                    }
                },
                modifier = Modifier.apply {
                    if (isSelected) border(width = 1.dp, color = Color.White)
                }
            ) {
                SmallIcon(
                    imageVector = Icons.Default.Home,
                    contentDescription = group.groupName!!
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            H1Text(
                text = group.groupName!!,
                color = AppTheme.colors.onSecondary,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GroupNotificationsButton(
    groupNotificationsAmount: Int,
    navigateGroupNotificationsOnClick: () -> Unit
) {
    BadgedBox(
        badge = {
            if (groupNotificationsAmount > 0) {
                Badge(
                    backgroundColor = AppTheme.colors.error,
                    modifier = Modifier
                        .offset((-15).dp, (15).dp)
                        .clip(AppTheme.shapes.circleShape)
                        .align(Alignment.Center)
                ) {
                    H1Text(
                        text = groupNotificationsAmount.toString(),
                        fontSize = 14.sp
                    )
                }
            }
        }
    ) {
        IconButton(onClick = navigateGroupNotificationsOnClick) {
            SmallIcon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications"
            )
        }
    }
}

@Composable
private fun MembersButton(
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
private fun GroupBalanceCard(
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
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimensions.spacing)
            ) {
                H1ConfirmTextButton(
                    text = "Debt",
                    onClick = navigateDebtActionAmountOnClick,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(AppTheme.dimensions.spacing))
                H1ConfirmTextButton(
                    text = "Settle",
                    enabled = myUserInfo.userBalance > 0,
                    onClick = navigateSettleActionAmountOnClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun ActiveSettleActions(
    activeSettleActions: List<SettleAction>,
    isMySettleAction: (SettleAction) -> Boolean,
    settleActionCardOnClick: (SettleAction) -> Unit,
    cardSize: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = modifier
    ) {
        H1Text(text = "Active Settle", fontWeight = FontWeight.Medium)
        LazyHorizontalGrid(
            rows = GridCells.Fixed(count = 2),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            modifier = Modifier.height(cardSize.times(2) + AppTheme.dimensions.appPadding)
        ) {
            items(
                activeSettleActions.sortedWith(
                    compareByDescending<SettleAction> { settleAction ->
                        isMySettleAction(settleAction)
                    }.thenBy { settleAction ->
                        settleAction.date
                    }
                )
            ) {settleAction ->
                SettleActionCard(
                    settleAction = settleAction,
                    settleActionCardOnClick = {
                        settleActionCardOnClick(settleAction)
                    },
                    isMySettleAction = isMySettleAction(settleAction),
                    cardSize = cardSize
                )
            }
        }
    }
}

@Composable
private fun SettleActionCard(
    settleAction: SettleAction,
    settleActionCardOnClick: () -> Unit,
    isMySettleAction: Boolean = false,
    cardSize: Dp = 140.dp
) {
    val pfpPainter = profilePicturePainter(settleAction.debteeUserInfo!!.profilePictureURL)

    BadgedBox(
        badge = {
            settleAction.transactionRecords.count { transactionRecord ->
                transactionRecord.dateAccepted == TransactionRecord.PENDING
            }.let { notificationCount ->
                if (isMySettleAction && notificationCount > 0) {
                    Badge(
                        backgroundColor = AppTheme.colors.error,
                        modifier = Modifier
                            .offset((-18).dp, (18.dp))
                            .clip(AppTheme.shapes.circleShape)
                            .align(Alignment.Center)
                    ) {
                        H1Text(
                            text = notificationCount.toString(),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    ) {
        Box(
            modifier = Modifier
                .clip(AppTheme.shapes.large)
                .width(cardSize)
                .height(cardSize)
                .background(
                    if (isMySettleAction) AppTheme.colors.confirm
                    else AppTheme.colors.secondary
                )
                .clickable(onClick = settleActionCardOnClick)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.cardPadding)
            ) {
                ProfileIcon(painter = pfpPainter, iconSize = 50.dp)
                MoneyAmount(moneyAmount = settleAction.remainingAmount, fontSize = 24.sp)
            }
        }
    }
}

@Composable
private fun DebtActionDetails(
    modifier: Modifier = Modifier,
    debtAction: DebtAction,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        UserInfoRowCard(
            userInfo = debtAction.debteeUserInfo!!,
            mainContent = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Caption(text = "Debt")
                    Caption(text = "${isoDate(debtAction.date)} at ${isoTime(debtAction.date)}")
                }
                H1Text(text = debtAction.debteeUserInfo!!.nickname!!, fontSize = 28.sp)
            },
            sideContent = null,
            iconSize = 80.dp
        )
        MoneyAmount(moneyAmount = debtAction.totalAmount, fontSize = 60.sp)
        H1Text(
            text = "\"" + debtAction.message + "\"",
            modifier = Modifier.padding(top = AppTheme.dimensions.spacingMedium)
        )
        Box(
            contentAlignment = Alignment.BottomStart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = AppTheme.dimensions.paddingMedium)
        ) {
            H1Text(
                text = "Transactions",
                modifier = Modifier.padding(top = AppTheme.dimensions.paddingMedium)
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(AppTheme.shapes.extraLarge)
                .background(AppTheme.colors.secondary)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                modifier = Modifier
                    .padding(vertical = AppTheme.dimensions.cardPadding)
                    .padding(horizontal = AppTheme.dimensions.rowCardPadding)
            ) {
                items(debtAction.transactionRecords) { transactionRecord ->
                    TransactionRecordRowCard(transactionRecord = transactionRecord)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SettleActionDetails(
    modifier: Modifier = Modifier,
    settleAction: SettleAction,
    myUserInfo: UserInfo,
    navigateSettleActionTransactionOnClick: () -> Unit,
    acceptSettleActionTransactionOnClick: (TransactionRecord) -> Unit
) {
    val scope = rememberCoroutineScope()
    val acceptPendingTransactionBottomSheetState =
        rememberModalBottomSheetState(initialValue = ModalBottomSheetValue.Hidden)

    val tabTitles: MutableList<String> = mutableListOf("Accepted")
    var selectedTabIndex: Int by remember { mutableStateOf(0) }
    var selectedTransaction: TransactionRecord? by remember { mutableStateOf(null) }

    val isMyAction: Boolean = (myUserInfo.userId!! == settleAction.debteeUserInfo!!.userId!!)
    if (
        isMyAction &&
        settleAction.remainingAmount > 0 &&
        settleAction.transactionRecords.any {
            it.dateAccepted == TransactionRecord.PENDING
        }
    ) {
        tabTitles.add("Pending")
    }
    selectedTabIndex = 0

    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
        selectedTransaction?.let { selectedTransaction ->
            BackPressModalBottomSheetLayout(
                sheetState = acceptPendingTransactionBottomSheetState,
                sheetContent = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement
                            .spacedBy(AppTheme.dimensions.spacingMedium),
                        modifier = modifier.fillMaxWidth()
                    ) {
                        H1Text(
                            text = "${selectedTransaction.debtorUserInfo!!.nickname!!} is " +
                                    "paying ${selectedTransaction.balanceChange!!.asMoneyAmount()}"
                        )
                        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
                            H1ConfirmTextButton(
                                text = "Accept",
                                scale = 0.8f,
                                onClick = {
                                    acceptSettleActionTransactionOnClick(selectedTransaction)
                                    scope.launch { acceptPendingTransactionBottomSheetState.hide() }
                                }
                            )
                        }
                    }
                },
                content = content
            )
        } ?: content()
    }

    modalSheets {
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = modifier.fillMaxSize()
        ) {
            UserInfoRowCard(
                userInfo = settleAction.debteeUserInfo!!,
                mainContent = {
                    Caption(text = settleAction.debteeUserInfo!!.nickname!!)
                    MoneyAmount(
                        moneyAmount = settleAction.remainingAmount,
                        fontSize = 60.sp
                    )
                },
                sideContent = {
                    Column(horizontalAlignment = Alignment.End) {
                        Caption(text = "Settle")
                        Caption(text = isoDate(settleAction.date))
                    }
                },
                iconSize = 80.dp
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = AppTheme.dimensions.paddingMedium)
            ) {
                tabTitles.forEachIndexed { index, tabTitle ->
                    if (tabTitle == tabTitles[selectedTabIndex]) {
                        H1Text(
                            text = tabTitle,
                            modifier = Modifier.clickable { selectedTabIndex = index }
                        )
                    } else {
                        Caption(
                            text = tabTitle,
                            modifier = Modifier.clickable { selectedTabIndex = index }
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(AppTheme.shapes.extraLarge)
                    .background(AppTheme.colors.secondary)
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                    modifier = Modifier
                        .padding(vertical = AppTheme.dimensions.cardPadding)
                        .padding(horizontal = AppTheme.dimensions.rowCardPadding)
                ) {
                    when (tabTitles[selectedTabIndex]) {
                        "Accepted" -> {
                            settleAction.transactionRecords.filter { transactionRecord ->
                                transactionRecord.isAccepted
                            }.let { acceptedTransactions ->
                                acceptedTransactions.forEach { acceptedTransaction ->
                                    TransactionRecordRowCard(
                                        transactionRecord = acceptedTransaction,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                        "Pending" -> {
                            settleAction.transactionRecords.filter { transactionRecord ->
                                transactionRecord.dateAccepted == TransactionRecord.PENDING
                            }.let { pendingTransactions ->
                                pendingTransactions.forEach { pendingTransaction ->
                                    TransactionRecordRowCard(
                                        transactionRecord = pendingTransaction,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                selectedTransaction = pendingTransaction
                                                scope.launch {
                                                    acceptPendingTransactionBottomSheetState
                                                        .show()
                                                }
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            H1ConfirmTextButton(
                text = "Settle",
                onClick = navigateSettleActionTransactionOnClick,
                enabled = !isMyAction &&
                        myUserInfo.userBalance < 0 &&
                        settleAction.remainingAmount > 0,
                modifier = Modifier.padding(AppTheme.dimensions.cardPadding)
            )
        }
    }
}

@Composable
private fun TransactionRecordRowCard(
    modifier: Modifier = Modifier,
    transactionRecord: TransactionRecord
) {
    UserInfoRowCard(
        userInfo = transactionRecord.debtorUserInfo!!,
        iconSize = 50.dp,
        mainContent = {
            H1Text(
                text = transactionRecord
                    .debtorUserInfo!!.nickname!!
            )
            Caption(
                text =
                    if (transactionRecord.isAccepted)
                        "Accepted on ${isoDate(transactionRecord.dateAccepted)}"
                    else
                        "Pending"
            )
        },
        sideContent = {
            MoneyAmount(
                moneyAmount = transactionRecord
                    .balanceChange!!,
                fontSize = 24.sp
            )
        },
        modifier = modifier
    )
}
