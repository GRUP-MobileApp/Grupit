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
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.*
import com.grup.ui.compose.asMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.*
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.compose.BackPressModalBottomSheetLayout
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.ProfileIcon
import com.grup.ui.compose.SmallIcon
import com.grup.ui.compose.UserInfoRowCard
import com.grup.ui.compose.recentActivityList
import com.grup.ui.models.TransactionActivity
import com.grup.ui.viewmodel.GroupDetailsViewModel
import com.grup.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

internal class GroupDetailsView : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val groupDetailsViewModel = getScreenModel<GroupDetailsViewModel>()

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            GroupDetailsLayout(
                groupDetailsViewModel = groupDetailsViewModel,
                navigator = navigator
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GroupDetailsLayout(
    groupDetailsViewModel: GroupDetailsViewModel,
    navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val actionDetailsBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val lazyListState = rememberLazyListState()

    val selectedGroup: Group = groupDetailsViewModel.selectedGroup
    val myUserInfo: UserInfo? by groupDetailsViewModel.myUserInfo.collectAsStateWithLifecycle()
    val groupActivity: List<TransactionActivity> by
        groupDetailsViewModel.groupActivity.collectAsStateWithLifecycle()
    val activeSettleActions: List<SettleAction> by
        groupDetailsViewModel.activeSettleActions.collectAsStateWithLifecycle()

    // Not getting updated
    var selectedAction: Action? by remember { mutableStateOf(null) }

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
                                                ActionAmountView(
                                                    actionType =
                                                        TransactionViewModel.SETTLE_TRANSACTION,
                                                    existingActionId = selectedAction.id
                                                )
                                            )
                                        }
                                    },
                                    acceptSettleActionTransactionOnClick = { transactionRecord ->
                                        groupDetailsViewModel.acceptSettleActionTransaction(
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
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(
                            onClick = { navigator.pop() }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = { H1Text(text = selectedGroup.groupName) },
                    actions = {
                        IconButton(
                            onClick = { navigator.push(GroupMembersView()) }
                        ) {
                            SmallIcon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Members"
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
            LazyColumn(
                state = lazyListState,
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
                modifier = Modifier
                    .padding(padding)
            ) {
                myUserInfo?.let { myUserInfo ->
                    item {
                        GroupBalanceCard(
                            modifier = Modifier
                                .fillMaxWidth(),
                            myUserInfo = myUserInfo,
                            navigateDebtActionAmountOnClick = {
                                navigator.push(
                                    ActionAmountView(actionType = TransactionViewModel.DEBT)
                                )
                            },
                            navigateSettleActionAmountOnClick = {
                                if (myUserInfo.userBalance > 0) {
                                    navigator.push(
                                        ActionAmountView(
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
                                    settleAction.debteeUserInfo.user.id == myUserInfo.user.id
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
            }
        }
    }
}

@Composable
private fun NoGroupsDisplay() {
    Text(text = " ^ Create or join a group", color = AppTheme.colors.onSecondary)
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
                    if (isSelected) border(width = 2.dp, color = Color.White)
                }
            ) {
                SmallIcon(imageVector = Icons.Default.Home, contentDescription = group.groupName)
            }
            Spacer(modifier = Modifier.width(20.dp))
            H1Text(
                text = group.groupName,
                color = AppTheme.colors.onSecondary,
                modifier = Modifier.weight(1f)
            )
        }
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
            rows = GridCells.Fixed(count = if (activeSettleActions.size >= 3) 2 else 1),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            modifier = Modifier.height(
                cardSize.times(if (activeSettleActions.size >= 3) 2 else 1) +
                        AppTheme.dimensions.appPadding
            )
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
                .size(cardSize)
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
                ProfileIcon(user = settleAction.debteeUserInfo.user, iconSize = 50.dp)
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
        UserRowCard(
            user = debtAction.debteeUserInfo.user,
            mainContent = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Caption(text = "Debt")
                    Caption(text = "${isoDate(debtAction.date)} at ${isoTime(debtAction.date)}")
                }
                H1Text(text = debtAction.debteeUserInfo.user.displayName, fontSize = 28.sp)
            },
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

    val isMyAction: Boolean =
        (myUserInfo.user.id == settleAction.debteeUserInfo.user.id)
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
                            text = "${selectedTransaction.debtorUserInfo.user.displayName} " +
                                    "is paying " +
                                    selectedTransaction.balanceChange.asMoneyAmount()
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
            UserRowCard(
                user = settleAction.debteeUserInfo.user,
                mainContent = {
                    Caption(text = settleAction.debteeUserInfo.user.displayName)
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
    UserRowCard(
        user = transactionRecord.debtorUserInfo.user,
        iconSize = 50.dp,
        mainContent = {
            H1Text(text = transactionRecord.debtorUserInfo.user.displayName)
            Caption(
                text =
                    if (transactionRecord.isAccepted)
                        "Accepted on ${isoDate(transactionRecord.dateAccepted)}"
                    else
                        "Pending"
            )
        },
        sideContent = {
            MoneyAmount(moneyAmount = transactionRecord.balanceChange, fontSize = 24.sp)
        },
        modifier = modifier
    )
}
