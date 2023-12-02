package com.grup.ui.compose.views

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.*
import com.grup.ui.*
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.models.TransactionActivity
import com.grup.ui.viewmodel.GroupDetailsViewModel
import kotlinx.coroutines.launch

internal class GroupDetailsView : Screen {
    override val key: ScreenKey = uniqueScreenKey
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

    val selectedGroup: Group = groupDetailsViewModel.selectedGroup!!
    val myUserInfo: UserInfo? by groupDetailsViewModel.myUserInfo.collectAsStateWithLifecycle()
    val groupActivity: List<TransactionActivity> by
        groupDetailsViewModel.groupActivity.collectAsStateWithLifecycle()
    val incomingActions: List<Action> by
        groupDetailsViewModel.incomingActions.collectAsStateWithLifecycle()

    // Not getting updated
    var selectedAction: Action? by remember { mutableStateOf(null) }
    val selectAction: (Action) -> Unit = { action ->
        selectedAction = action
        scope.launch { actionDetailsBottomSheetState.show() }
    }

    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
        selectedAction?.let { selectedAction ->
            ActionDetailsBottomSheet(
                sheetState = actionDetailsBottomSheetState,
                action = selectedAction,
                isMyTransactionRecord = { transactionRecord ->
                    transactionRecord.userInfo.user.id == myUserInfo?.user?.id
                            && transactionRecord.isPending
                },
                acceptAction = { action ->
                    groupDetailsViewModel.acceptAction(
                        action = action,
                        onSuccess = {
                            scope.launch { actionDetailsBottomSheetState.hide() }
                        },
                        onError = { }
                    )
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
                    title = { H1Header(text = selectedGroup.groupName) },
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
                                navigator.push(DebtActionView())
                            },
                            navigateSettleActionAmountOnClick = {
                                if (myUserInfo.userBalance < 0) {
                                    navigator.push(SettleActionView())
                                }
                            }
                        )
                        Spacer(modifier = Modifier.height(AppTheme.dimensions.spacingLarge))
                    }
                    if (incomingActions.isNotEmpty()) {
                        item {
                            IncomingActions(
                                actions = incomingActions,
                                actionCardOnClick = selectAction,
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
                    text = "Request",
                    onClick = navigateDebtActionAmountOnClick,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(AppTheme.dimensions.cardPadding))
                H1ConfirmTextButton(
                    text = "Pay",
                    enabled = myUserInfo.userBalance < 0,
                    onClick = navigateSettleActionAmountOnClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun IncomingActions(
    actions: List<Action>,
    actionCardOnClick: (Action) -> Unit,
    cardSize: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = modifier
    ) {
        H1Header(text = "Incoming", fontWeight = FontWeight.Medium)
        LazyHorizontalGrid(
            rows = GridCells.Fixed(count = if (actions.size >= 3) 2 else 1),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            modifier = Modifier.height(
                cardSize.times(if (actions.size >= 3) 2 else 1) +
                        AppTheme.dimensions.appPadding
            )
        ) {
            items(actions) {action ->
                Column(
                    verticalArrangement = Arrangement.SpaceAround,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .size(cardSize)
                        .clip(AppTheme.shapes.large)
                        .background(AppTheme.colors.secondary)
                        .clickable { actionCardOnClick(action) }
                        .padding(AppTheme.dimensions.cardPadding)
                ) {
                    ProfileIcon(user = action.userInfo.user, iconSize = 50.dp)
                    Column {
                        Caption(
                            text = when(action) {
                                is DebtAction -> "Request"
                                is SettleAction -> "Pay"
                            }
                        )
                        MoneyAmount(moneyAmount = action.totalAmount, fontSize = 24.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ActionDetails(
    modifier: Modifier = Modifier,
    action: Action,
    isMyTransactionRecord: (TransactionRecord) -> Boolean,
    acceptAction: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxSize()
    ) {
        UserRowCard(
            user = action.userInfo.user,
            mainContent = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Caption(
                        text = when(action) {
                            is DebtAction -> "Request"
                            is SettleAction -> "Pay"
                        }
                    )
                    Caption(
                        text = "${isoDate(action.date)} at ${isoTime(action.date)}",
                        fontSize = AppTheme.typography.tinyFont
                    )
                }
                H1Text(text = action.userInfo.user.displayName, fontSize = 28.sp)
            },
            iconSize = 80.dp
        )
        MoneyAmount(moneyAmount = action.totalAmount, fontSize = 60.sp)
        if (action is DebtAction) {
            H1Text(
                text = "\"${action.message}\"",
                modifier = Modifier.padding(vertical = AppTheme.dimensions.spacingMedium)
            )
        }
        H1Header(text = "Transactions", modifier = Modifier.align(Alignment.Start))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(AppTheme.shapes.extraLarge)
                .background(AppTheme.colors.secondary)
        ) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.cardPadding),
                contentPadding = PaddingValues(
                    horizontal = AppTheme.dimensions.rowCardPadding,
                    vertical = AppTheme.dimensions.cardPadding
                )
            ) {
                items(
                    action.transactionRecords.sortedBy { isMyTransactionRecord(it) }
                ) { transactionRecord ->
                    TransactionRecordRowCard(
                        transactionRecord = transactionRecord,
                        isMyTransactionRecord = isMyTransactionRecord(transactionRecord)
                    )
                }
            }
        }
        if (action.transactionRecords.any { isMyTransactionRecord(it) }) {
            Spacer(modifier = Modifier.weight(1f))
            H1ConfirmTextButton(
                text = "Accept",
                onClick = acceptAction
            )
        }
    }
}

@Composable
private fun TransactionRecordRowCard(
    modifier: Modifier = Modifier,
    transactionRecord: TransactionRecord,
    isMyTransactionRecord: Boolean = false
) {
    UserRowCard(
        user = transactionRecord.userInfo.user,
        iconSize = 50.dp,
        mainContent = {
            H1Text(text = transactionRecord.userInfo.user.displayName)
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
                moneyAmount = transactionRecord.balanceChange,
                fontSize = 24.sp,
                color = if (isMyTransactionRecord) AppTheme.colors.confirm
                        else AppTheme.colors.onSecondary
            )
        },
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ActionDetailsBottomSheet(
    action: Action,
    isMyTransactionRecord: (TransactionRecord) -> Boolean,
    acceptAction: (Action) -> Unit,
    sheetState: ModalBottomSheetState,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()

    BackPressModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            BackPressScaffold(
                onBackPress = {
                    scope.launch { sheetState.hide() }
                }
            ) { padding ->
                ActionDetails(
                    action = action,
                    isMyTransactionRecord = isMyTransactionRecord,
                    acceptAction = { acceptAction(action) },
                    modifier = Modifier
                        .padding(padding)
                        .padding(AppTheme.dimensions.appPadding)
                )
            }
        },
        content = content
    )
}
