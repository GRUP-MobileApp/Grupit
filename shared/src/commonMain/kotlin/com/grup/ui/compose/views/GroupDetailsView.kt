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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.*
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.models.TransactionActivity
import com.grup.ui.viewmodel.GroupDetailsViewModel

internal class GroupDetailsView(
    private val groupId: String,
) : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val groupDetailsViewModel = rememberScreenModel { GroupDetailsViewModel(groupId) }

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

@Composable
private fun GroupDetailsLayout(
    groupDetailsViewModel: GroupDetailsViewModel,
    navigator: Navigator
) {
    val lazyListState = rememberLazyListState()

    val myUserInfo: UserInfo by groupDetailsViewModel.myUserInfo.collectAsStateWithLifecycle()
    val groupActivity: List<TransactionActivity> by
        groupDetailsViewModel.groupActivity.collectAsStateWithLifecycle()
    val activeSettleActions: List<SettleAction> by
        groupDetailsViewModel.activeSettleActions.collectAsStateWithLifecycle()
    val incomingDebtActions: List<Pair<DebtAction, TransactionRecord>> by
        groupDetailsViewModel.incomingDebtActions.collectAsStateWithLifecycle()

    val selectedGroup: Group = myUserInfo.group
    val selectAction: (Action) -> Unit = { action ->
        when(action) {
            is DebtAction -> {
                navigator.push(DebtActionDetailsView(action.id))
            }
            is SettleAction -> {
                navigator.push(
                    SettleActionDetailsView(selectedGroup.id, action.id)
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navigator.pop() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { H1Header(text = selectedGroup.groupName) },
                actions = {
                    IconButton(
                        onClick = {
                            navigator.push(
                                GroupMembersView(groupDetailsViewModel.selectedGroupId)
                            )
                        }
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
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
            modifier = Modifier
                .padding(padding)
        ) {
            item {
                GroupBalanceCard(
                    modifier = Modifier
                        .fillMaxWidth(),
                    myUserInfo = myUserInfo,
                    navigateDebtActionAmountOnClick = {
                        navigator.push(DebtActionView(groupDetailsViewModel.selectedGroupId))
                    },
                    navigateSettleActionAmountOnClick = {
                        if (myUserInfo.userBalance > 0) {
                            navigator.push(SettleActionView(groupDetailsViewModel.selectedGroupId))
                        }
                    }
                )
            }
            if (activeSettleActions.isNotEmpty()) {
                item {
                    H1Text(text = "Active Settle Requests", fontWeight = FontWeight.Medium)
                }
                item {
                    activeSettleActions.partition { settleAction ->
                        settleAction.userInfo.user.id == myUserInfo.user.id
                    }.let { (myActiveSettleActions, activeSettleActions) ->
                        ActiveSettleActions(
                            myActiveSettleActions = myActiveSettleActions,
                            activeSettleActions = activeSettleActions,
                            settleActionCardOnClick = selectAction,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            if (incomingDebtActions.isNotEmpty()) {
                item {
                    H1Header(text = "Incoming Debt Requests", fontWeight = FontWeight.Medium)
                }
                item {
                    IncomingDebtActions(
                        incomingDebtActions = incomingDebtActions,
                        debtActionCardOnClick = selectAction,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            item {
                H1Header(
                    text = "Recent Transactions",
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            groupActivity.groupBy {
                isoFullDate(it.date)
            }.forEach { (date, transactionActivityByDate) ->
                item {
                    Caption(text = date)
                }
                items(transactionActivityByDate) { transactionActivity ->
                    TransactionActivityRowCard(
                        transactionActivity = transactionActivity,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(AppTheme.shapes.large)
                            .background(AppTheme.colors.secondary)
                            .clickable { selectAction(transactionActivity.action) }
                            .padding(AppTheme.dimensions.rowCardPadding)
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
            MoneyAmount(
                moneyAmount = myUserInfo.userBalance,
                color = if (myUserInfo.userBalance >= 0) AppTheme.colors.confirm
                        else AppTheme.colors.deny,
                fontWeight = FontWeight.SemiBold
            )
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
                H1DenyTextButton(
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
    myActiveSettleActions: List<SettleAction>,
    activeSettleActions: List<SettleAction>,
    settleActionCardOnClick: (SettleAction) -> Unit,
    cardSize: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = modifier
    ) {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(count = if (activeSettleActions.size >= 3) 2 else 1),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            modifier = Modifier.height(
                cardSize.times(
                    if (activeSettleActions.size >= 3) 2 else 1
                ) + AppTheme.dimensions.appPadding
            )
        ) {
            items(myActiveSettleActions) { settleAction ->
                SettleActionCard(
                    settleAction = settleAction,
                    settleActionCardOnClick = {
                        settleActionCardOnClick(settleAction)
                    },
                    isMySettleAction = true,
                    cardSize = cardSize
                )
            }
            items(activeSettleActions) {settleAction ->
                SettleActionCard(
                    settleAction = settleAction,
                    settleActionCardOnClick = {
                        settleActionCardOnClick(settleAction)
                    },
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
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .size(cardSize)
            .clip(AppTheme.shapes.large)
            .background(
                if (isMySettleAction) AppTheme.colors.confirm
                else AppTheme.colors.secondary
            )
            .clickable(onClick = settleActionCardOnClick)
            .padding(AppTheme.dimensions.cardPadding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),) {
            ProfileIcon(user = settleAction.userInfo.user, iconSize = 50.dp)
            MoneyAmount(moneyAmount = settleAction.remainingAmount, fontSize = 24.sp)
        }
    }
}

@Composable
private fun IncomingDebtActions(
    incomingDebtActions: List<Pair<DebtAction, TransactionRecord>>,
    debtActionCardOnClick: (DebtAction) -> Unit,
    cardSize: Dp = 140.dp,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
        modifier = modifier
    ) {
        LazyHorizontalGrid(
            rows = GridCells.Fixed(count = if (incomingDebtActions.size >= 3) 2 else 1),
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
            modifier = Modifier.height(
                cardSize.times(
                    if (incomingDebtActions.size >= 3) 2 else 1
                ) + AppTheme.dimensions.appPadding
            )
        ) {
            items(incomingDebtActions) { (debtAction, transactionRecord) ->
                DebtActionCard(
                    debtAction = debtAction,
                    transactionRecord = transactionRecord,
                    debtActionCardOnClick = {
                        debtActionCardOnClick(debtAction)
                    },
                    cardSize = cardSize
                )
            }
        }
    }
}

@Composable
private fun DebtActionCard(
    debtAction: DebtAction,
    transactionRecord: TransactionRecord,
    debtActionCardOnClick: () -> Unit,
    cardSize: Dp = 140.dp
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .size(cardSize)
            .clip(AppTheme.shapes.large)
            .background(AppTheme.colors.deny)
            .clickable(onClick = debtActionCardOnClick)
            .padding(AppTheme.dimensions.cardPadding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),) {
            ProfileIcon(user = debtAction.userInfo.user, iconSize = 50.dp)
            MoneyAmount(moneyAmount = transactionRecord.balanceChange, fontSize = 24.sp)
        }
    }
}
