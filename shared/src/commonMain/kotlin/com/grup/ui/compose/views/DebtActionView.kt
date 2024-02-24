package com.grup.ui.compose.views

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch

internal class DebtActionView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val transactionViewModel = getScreenModel<TransactionViewModel>()
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            DebtActionLayout(
                transactionViewModel = transactionViewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun DebtActionLayout(
    transactionViewModel: TransactionViewModel,
    navigator: Navigator
) {
    var currentPage: Int by remember { mutableStateOf(0) }

    val userInfos: List<UserInfo> by transactionViewModel.userInfos.collectAsStateWithLifecycle()

    var debtActionAmount: String by remember { mutableStateOf("0") }
    var message: String by remember { mutableStateOf("") }

    AnimatedContent(
        targetState = currentPage,
        transitionSpec = {
            if (targetState > initialState) {
                (slideInHorizontally { height -> height } + fadeIn()).togetherWith(
                    slideOutHorizontally { height -> -height } + fadeOut())
            } else {
                (slideInHorizontally { height -> -height } + fadeIn()).togetherWith(
                    slideOutHorizontally { height -> height } + fadeOut())
            }.using(
                SizeTransform(clip = false)
            )
        }
    ) { page ->
        when(page) {
            0 -> DebtActionKeypadPage(
                debtActionAmount = debtActionAmount,
                onDebtActionAmountChange = { newMoneyAmount ->
                    debtActionAmount =
                        if (newMoneyAmount.toDouble() > 999999999) {
                            999999999.toString()
                        } else {
                            newMoneyAmount
                        }
                },
                message = message,
                onMessageChange = { message = it },
                onBackPress = { navigator.pop() },
                changePageDebtActionDetails = { currentPage = 1 }
            )
            1 -> DebtActionDetailsPage(
                userInfos = userInfos,
                debtActionAmount = debtActionAmount.toDouble(),
                onBackPress = { currentPage = 0 },
                createDebtAction = { debtActionAmounts ->
                    transactionViewModel.createDebtAction(debtActionAmounts, message)
                    navigator.pop()
                }
            )
        }
    }

}

@Composable
private fun DebtActionKeypadPage(
    debtActionAmount: String,
    onDebtActionAmountChange: (String) -> Unit,
    message: String,
    onMessageChange: (String) -> Unit,
    onBackPress: () -> Unit,
    changePageDebtActionDetails: () -> Unit
) {
    KeyPadScreenLayout(
        moneyAmount = debtActionAmount,
        onMoneyAmountChange = { onDebtActionAmountChange(it) },
        message = message,
        onMessageChange = { onMessageChange(it) },
        confirmButton = {
            H1ConfirmTextButton(
                text = "Request",
                enabled = debtActionAmount.toDouble() > 0 && message.isNotBlank(),
                onClick = changePageDebtActionDetails
            )
        },
        onBackPress = onBackPress
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DebtActionDetailsPage(
    userInfos: List<UserInfo>,
    debtActionAmount: Double,
    onBackPress: () -> Unit,
    createDebtAction: (Map<UserInfo, Double>) -> Unit
) {
    val addDebtorBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val debtAmountBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )
    val scope = rememberCoroutineScope()

    var splitStrategy: TransactionViewModel.SplitStrategy by remember {
        mutableStateOf(TransactionViewModel.SplitStrategy.EvenSplit)
    }

    val rawSplitStrategyDebtAmounts:
            SnapshotStateMap<UserInfo, Double?> = remember { mutableStateMapOf() }
    val splitStrategyDebtAmounts: Map<UserInfo, Double> =
        splitStrategy.generateSplit(debtActionAmount, rawSplitStrategyDebtAmounts)

    var keyPadUserInfo: UserInfo? by remember { mutableStateOf(null) }

    LaunchedEffect(debtActionAmount) {
        rawSplitStrategyDebtAmounts.clear()
    }

    val modalSheets: @Composable (@Composable () -> Unit) -> Unit = { content ->
        AddDebtorBottomSheet(
            userInfos = userInfos,
            addDebtorsOnClick = { selectedUsers ->
                rawSplitStrategyDebtAmounts.keys.minus(selectedUsers).forEach { userInfo ->
                    rawSplitStrategyDebtAmounts.remove(userInfo)
                }
                selectedUsers.minus(rawSplitStrategyDebtAmounts.keys).forEach { userInfo ->
                    rawSplitStrategyDebtAmounts[userInfo] = null
                }
                scope.launch { addDebtorBottomSheetState.hide() }
            },
            state = addDebtorBottomSheetState
        ) {
            keyPadUserInfo?.let { userInfo ->
                KeyPadBottomSheet(
                    state = debtAmountBottomSheetState,
                    initialMoneyAmount = rawSplitStrategyDebtAmounts[userInfo] ?: 0.0,
                    maxMoneyAmount = debtActionAmount,
                    onClick = { debtAmount ->
                        rawSplitStrategyDebtAmounts[userInfo] =
                            if (debtAmount > 0.0) debtAmount else null
                    },
                    onBackPress = {
                        scope.launch { debtAmountBottomSheetState.hide() }
                    },
                    content = content
                )
            } ?: content()
        }
    }

    modalSheets {
        BackPressScaffold(onBackPress = onBackPress) { padding ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppTheme.colors.primary)
                    .padding(padding)
                    .padding(AppTheme.dimensions.appPadding)
            ) {
                MoneyAmount(
                    moneyAmount = debtActionAmount,
                    fontSize = 100.sp
                )
                Box(
                    contentAlignment = Alignment.TopCenter,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(AppTheme.shapes.large)
                        .background(AppTheme.colors.secondary)
                        .padding(bottom = AppTheme.dimensions.cardPadding)
                        .padding(horizontal = AppTheme.dimensions.cardPadding)
                ) {
                    Column(
                        verticalArrangement = Arrangement
                            .spacedBy(AppTheme.dimensions.spacing),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        DebtActionSettings(
                            splitStrategy = splitStrategy,
                            onSplitStrategyChange = { newSplitStrategy ->
                                if (splitStrategy::class != newSplitStrategy::class) {
                                    splitStrategy = newSplitStrategy
                                    rawSplitStrategyDebtAmounts
                                        .mapValuesTo(rawSplitStrategyDebtAmounts) { null }
                                }
                            },
                            addDebtorsOnClick = {
                                scope.launch { addDebtorBottomSheetState.show() }
                            }
                        )
                        UserInfoAmountsList(
                            userInfoMoneyAmounts = splitStrategyDebtAmounts,
                            userInfoHasSetAmount = { userInfo ->
                                if (!splitStrategy.editable) {
                                    true
                                } else {
                                    rawSplitStrategyDebtAmounts[userInfo] != null
                                }
                            },
                            userInfoAmountOnClick = { userInfo ->
                                if (splitStrategy.editable) {
                                    keyPadUserInfo = userInfo
                                    scope.launch { debtAmountBottomSheetState.show() }
                                }
                            },
                            modifier = Modifier.weight(1f)
                        )
                        H1ConfirmTextButton(
                            text = "Create",
                            enabled = splitStrategy.isValid(
                                debtActionAmount,
                                splitStrategyDebtAmounts
                            ),
                            onClick = {
                                createDebtAction(
                                    splitStrategy.splitToMoneyAmounts(
                                        debtActionAmount,
                                        splitStrategyDebtAmounts
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DebtActionSettings(
    splitStrategy: TransactionViewModel.SplitStrategy,
    onSplitStrategyChange: (TransactionViewModel.SplitStrategy) -> Unit,
    addDebtorsOnClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            H1Text(text = "by ")
            Box(
                modifier = Modifier
                    .clip(AppTheme.shapes.medium)
                    .background(AppTheme.colors.primary)
                    .clickable {
                        // TODO: Make actual split strategy selection screen
                        when(splitStrategy) {
                            is TransactionViewModel.SplitStrategy.EvenSplit -> {
                                onSplitStrategyChange(
                                    TransactionViewModel.SplitStrategy.UnevenSplit
                                )
                            }
                            is TransactionViewModel.SplitStrategy.UnevenSplit -> {
                                onSplitStrategyChange(TransactionViewModel.SplitStrategy.EvenSplit)
                            }
                            else -> {}
                        }
                    }
                    .padding(AppTheme.dimensions.spacingSmall)
            ) {
                H1Text(text = splitStrategy.name)
            }
            H1Text(text = " between:")
        }
        AddDebtorButton(addDebtorsOnClick = addDebtorsOnClick)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AddDebtorBottomSheet(
    state: ModalBottomSheetState,
    userInfos: List<UserInfo>,
    addDebtorsOnClick: (Set<UserInfo>) -> Unit,
    textColor: Color = AppTheme.colors.onSecondary,
    content: @Composable () -> Unit
) {
    var selectedUsers: Set<UserInfo> by remember { mutableStateOf(emptySet()) }
    var usernameSearchQuery: String by remember { mutableStateOf("") }

    BackPressModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.8f)
                    .padding(AppTheme.dimensions.paddingMedium)
            ) {
                H1Text(text = "Add Debtors", color = textColor, fontSize = 50.sp)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UsernameSearchBar(
                        usernameSearchQuery = usernameSearchQuery,
                        onQueryChange = { usernameSearchQuery = it },
                        modifier = Modifier.weight(1f)
                    )
                    Button(
                        onClick = { addDebtorsOnClick(selectedUsers) },
                        shape = AppTheme.shapes.circleShape,
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.colors.confirm
                        ),
                        modifier = Modifier
                            .width(100.dp)
                            .height(50.dp)
                    ) {
                        Text(
                            text = "Add",
                            color = AppTheme.colors.onSecondary
                        )
                    }
                }
                Spacer(modifier = Modifier.height(AppTheme.dimensions.spacing))
                SelectDebtorsChecklist(
                    userInfos = userInfos.filter { userInfo ->
                        userInfo.user.displayName.contains(usernameSearchQuery, ignoreCase = true)
                    },
                    selectedUsers = selectedUsers,
                    onCheckedChange = { userInfo, isSelected ->
                        selectedUsers = if (isSelected) {
                            selectedUsers + userInfo
                        } else {
                            selectedUsers - userInfo
                        }
                    }
                )
            }
        },
        content = content
    )
}

@Composable
private fun SelectDebtorsChecklist(
    userInfos: List<UserInfo>,
    selectedUsers: Set<UserInfo>,
    onCheckedChange: (UserInfo, Boolean) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(userInfos) { userInfo ->
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserRowCard(
                    user = userInfo.user,
                    mainContent = {
                        H1Text(text = userInfo.user.displayName)
                        Caption(text = "Balance: ${userInfo.userBalance.asMoneyAmount()}")
                    },
                    sideContent = {
                        Checkbox(
                            checked = selectedUsers.contains(userInfo),
                            onCheckedChange = { isChecked -> onCheckedChange(userInfo, isChecked) },
                            colors = CheckboxDefaults.colors(
                                uncheckedColor = AppTheme.colors.onSecondary
                            )
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun AddDebtorButton(
    addDebtorsOnClick: () -> Unit
) {
    IconButton(onClick = addDebtorsOnClick) {
        SmallIcon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add a debtor"
        )
    }
}
