package com.grup.ui.compose.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.Action
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1DenyTextButton
import com.grup.ui.compose.H1Header
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.ModalBottomSheetLayout
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.PagerArrowRow
import com.grup.ui.compose.ProfileIcon
import com.grup.ui.compose.SmallIcon
import com.grup.ui.compose.TransactionActivityRowCard
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoFullDate
import com.grup.ui.models.TransactionActivity
import com.grup.ui.viewmodel.GroupDetailsViewModel
import kotlinx.coroutines.launch

internal class GroupDetailsView(private val groupId: String) : Screen {
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun GroupDetailsLayout(
    groupDetailsViewModel: GroupDetailsViewModel,
    navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val lazyListState = rememberLazyListState()
    val tutorialBottomSheetState =
        rememberModalBottomSheetState(
            initialValue = ModalBottomSheetValue.Hidden,
            confirmValueChange = { false }
        )

    LaunchedEffect(true) {
        if (!groupDetailsViewModel.hasViewedTutorial) {
            scope.launch { tutorialBottomSheetState.show() }
            groupDetailsViewModel.hasViewedTutorial = true
        }
    }

    val myUserInfo: UserInfo by groupDetailsViewModel.myUserInfo.collectAsStateWithLifecycle()
    val groupActivity: List<TransactionActivity> by
        groupDetailsViewModel.groupActivity.collectAsStateWithLifecycle()
    val activeSettleActions: List<SettleAction> by
        groupDetailsViewModel.activeSettleActions.collectAsStateWithLifecycle()
    val incomingDebtActions: List<Pair<DebtAction, TransactionRecord>> by
        groupDetailsViewModel.incomingDebtActions.collectAsStateWithLifecycle()

    val selectedGroup: Group = myUserInfo.group
    val selectAction: (Action) -> Unit = { action ->
        navigator.push(ActionDetailsView(action.id))
    }

    TutorialBottomSheet(
        sheetState = tutorialBottomSheetState,
        onTutorialFinish = {
            scope.launch { tutorialBottomSheetState.hide() }
            groupDetailsViewModel.hasViewedTutorial = true
        }
    ) {
        BackPressScaffold(
            title = selectedGroup.groupName,
            onBackPress = { navigator.pop() },
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
            }
        ) { padding ->
            LazyColumn(
                state = lazyListState,
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
                modifier = Modifier.padding(padding)
            ) {
                item {
                    GroupBalanceCard(
                        modifier = Modifier.fillMaxWidth(),
                        myUserInfo = myUserInfo,
                        navigateDebtActionAmountOnClick = {
                            navigator.push(DebtActionView(groupDetailsViewModel.selectedGroupId))
                        },
                        navigateSettleActionAmountOnClick = {
                            if (myUserInfo.userBalance > 0) {
                                navigator.push(
                                    SettleActionView(groupDetailsViewModel.selectedGroupId)
                                )
                            }
                        }
                    )
                }
                if (activeSettleActions.isNotEmpty()) {
                    item {
                        H1Header(text = "Active Settle", fontWeight = FontWeight.Medium)
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
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                groupActivity.groupBy {
                    isoFullDate(it.date)
                }.forEach { (date, transactionActivityByDate) ->
                    item { Caption(text = date) }
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
                fontWeight = FontWeight.SemiBold,
                fontSize = AppTheme.typography.moneyAmountFont.times(1.25f)
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
    cardSize: Dp = AppTheme.dimensions.actionCardSize,
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
                if (activeSettleActions.size >= 3) {
                    AppTheme.dimensions.appPadding + cardSize.times(2)
                } else {
                    cardSize
                }
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
    cardSize: Dp = AppTheme.dimensions.actionCardSize
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .size(cardSize)
            .aspectRatio(1f)
            .clip(AppTheme.shapes.large)
            .background(
                if (isMySettleAction) AppTheme.colors.confirm
                else AppTheme.colors.secondary
            )
            .clickable(onClick = settleActionCardOnClick)
            .padding(AppTheme.dimensions.cardPadding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing)) {
            ProfileIcon(user = settleAction.userInfo.user)
            Caption(text = "@${settleAction.userInfo.user.venmoUsername}")
            MoneyAmount(moneyAmount = settleAction.remainingAmount)
        }
    }
}

@Composable
private fun IncomingDebtActions(
    incomingDebtActions: List<Pair<DebtAction, TransactionRecord>>,
    debtActionCardOnClick: (DebtAction) -> Unit,
    cardSize: Dp = AppTheme.dimensions.actionCardSize,
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
                if (incomingDebtActions.size >= 3) {
                    AppTheme.dimensions.appPadding + cardSize.times(2)
                } else {
                    cardSize
                }
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
    cardSize: Dp = AppTheme.dimensions.actionCardSize
) {
    Box(
        contentAlignment = Alignment.CenterStart,
        modifier = Modifier
            .size(cardSize)
            .aspectRatio(1f)
            .clip(AppTheme.shapes.large)
            .background(AppTheme.colors.deny)
            .clickable(onClick = debtActionCardOnClick)
            .padding(AppTheme.dimensions.cardPadding)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge)) {
            ProfileIcon(user = debtAction.userInfo.user)
            MoneyAmount(moneyAmount = transactionRecord.balanceChange)
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
private fun TutorialBottomSheet(
    sheetState: ModalBottomSheetState,
    onTutorialFinish: () -> Unit,
    content: @Composable () -> Unit
) {
    val scope = rememberCoroutineScope()
    val pageCount = 4
    val pagerState = rememberPagerState(pageCount = { pageCount })

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetContent = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
                    .padding(AppTheme.dimensions.appPadding)
            ) {
                HorizontalPager(state = pagerState) { page ->
                    Column(
                        verticalArrangement =
                            Arrangement.spacedBy(AppTheme.dimensions.spacingMedium)
                    ) {
                        when(page) {
                            0 -> {
                                H1Header(
                                    text = "Quick Guide",
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = AppTheme.typography.largeHeaderFont
                                )
                                Caption(text = "Let's get started!")
                            }
                            1 -> {
                                H1Header(text = "Balance")
                                Caption(
                                    text = "The value of your balance is relative to the group: " +
                                            "a positive balance means you are owed money overall, " +
                                            "while a negative balance means you owe money overall.",
                                    maxLines = Int.MAX_VALUE
                                )
                                H1Text(
                                    text = "Grupit simplifies complex person-to-person debts into " +
                                            "a single balance within a group."
                                )
                            }
                            2 -> {
                                H1Header(text = "Creating Debt")
                                Caption(
                                    text = "When there is a real-life exchange of money, " +
                                            "use a Debt request to manage and track new debts.",
                                    maxLines = Int.MAX_VALUE
                                )
                                H1Text(
                                    text = "You can choose to keep track of debts using Grupit " +
                                            "balance, or use a third-party application such as " +
                                            "Venmo to instantly settle on debts."
                                )
                            }
                            3 -> {
                                H1Header(text = "Requesting Money")
                                Caption(
                                    text = "If you choose to use Grupit balance, you create an IOU " +
                                            "that can be used later in a Settle request.",
                                    maxLines = Int.MAX_VALUE
                                )
                                H1Text(
                                    text = "When you have positive balance and want to settle up, " +
                                            "create a Settle request to the group with the " +
                                            "desired amount."
                                )
                            }
                            4 -> {
                                H1Header(text = "Settling Debt")
                                Caption(
                                    text = "Members with negative balances can pay others back " +
                                            "on active Settle requests."
                                )
                                H1Text(
                                    text = "Create a Settle transaction and pay others back using " +
                                            "third-party services such as Venmo. When the " +
                                            "original requester confirms your payment, Grupit " +
                                            "balances will change to reflect the settled debt."
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                PagerArrowRow(
                    pagerState = pagerState,
                    onClickNext = { page ->
                        scope.launch {
                            if (page == pagerState.pageCount - 1) {
                                onTutorialFinish()
                            } else {
                                pagerState.animateScrollToPage(page + 1)
                            }
                        }
                    }
                )
            }
        },
        content = content
    )
}
