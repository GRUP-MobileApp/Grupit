package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.UserInfo
import com.grup.ui.compose.asMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.*
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.compose.BackPressModalBottomSheetLayout
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.SmallIcon
import com.grup.ui.compose.UserInfoRowCard
import com.grup.ui.compose.UsernameSearchBar
import com.grup.ui.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

internal class DebtActionView(
    private val debtActionAmount: Double,
    private val message: String
) : Screen {
    @Composable
    override fun Content() {
        val transactionViewModel: TransactionViewModel =
            rememberScreenModel { TransactionViewModel() }
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            DebtActionLayout(
                transactionViewModel = transactionViewModel,
                navigator = navigator,
                debtActionAmount = debtActionAmount,
                message = message
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DebtActionLayout(
    transactionViewModel: TransactionViewModel,
    navigator: Navigator,
    debtActionAmount: Double,
    message: String
) {
    val addDebtorBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val debtAmountBottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val scope = rememberCoroutineScope()

    val userInfos: List<UserInfo> by transactionViewModel.userInfos.collectAsStateWithLifecycle()
    var splitStrategy: TransactionViewModel.SplitStrategy by remember {
        mutableStateOf(TransactionViewModel.SplitStrategy.EvenSplit)
    }

    val rawSplitStrategyDebtAmounts:
            SnapshotStateMap<UserInfo, Double?> = remember { mutableStateMapOf() }
    val splitStrategyDebtAmounts: Map<UserInfo, Double> =
        splitStrategy.generateSplit(debtActionAmount, rawSplitStrategyDebtAmounts)

    var keyPadUserInfo: UserInfo? by remember { mutableStateOf(null) }
    var keyPadInitialDebtAmount: Double by remember { mutableStateOf(0.0) }

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
                    initialMoneyAmount = keyPadInitialDebtAmount,
                    isEnabled = { debtAmount ->
                        debtAmount <= splitStrategyDebtAmounts.values.sum()
                    },
                    onClick = { debtAmount ->
                        rawSplitStrategyDebtAmounts[userInfo] = debtAmount
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
        Scaffold(
            topBar = {
                DebtActionTopBar(
                    onBackPress = { navigator.pop() }
                )
            }
        ) { padding ->
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
                        SelectedDebtorsList(
                            splitStrategyDebtAmounts = splitStrategyDebtAmounts,
                            userHasSetDebtAmount = { userInfo ->
                                if (!splitStrategy.editable) {
                                    true
                                } else {
                                    rawSplitStrategyDebtAmounts[userInfo] != null
                                }
                            },
                            debtAmountOnClick = { userInfo, debtAmount ->
                                if (splitStrategy.editable) {
                                    keyPadUserInfo = userInfo
                                    keyPadInitialDebtAmount = debtAmount
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
                                transactionViewModel.createDebtAction(
                                    splitStrategy.splitToMoneyAmounts(
                                        debtActionAmount,
                                        splitStrategyDebtAmounts
                                    ),
                                    message
                                )
                                navigator.pop()
                                navigator.pop()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DebtActionTopBar(
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = { },
        backgroundColor = AppTheme.colors.primary,
        navigationIcon = {
            IconButton(
                onClick = onBackPress
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = AppTheme.colors.onSecondary
                )
            }
        }
    )
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
            H1Text(text = "by ", fontSize = 20.sp)
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
                H1Text(
                    text = splitStrategy.name,
                    fontSize = 20.sp
                )
            }
            H1Text(text = " between:", fontSize = 20.sp)
        }
        AddDebtorButton(addDebtorsOnClick = addDebtorsOnClick)
    }
}

@Composable
private fun SelectedDebtorsList(
    splitStrategyDebtAmounts: Map<UserInfo, Double>,
    userHasSetDebtAmount: (UserInfo) -> Boolean,
    debtAmountOnClick: ((UserInfo, Double) -> Unit),
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(splitStrategyDebtAmounts.keys.toTypedArray()) { userInfo ->
                UserInfoRowCard(
                    userInfo = userInfo,
                    sideContent = {
                        splitStrategyDebtAmounts[userInfo]?.let { debtAmount ->
                            MoneyAmount(
                                moneyAmount = debtAmount,
                                moneyAmountTextColor =
                                    if (userHasSetDebtAmount(userInfo))
                                        AppTheme.colors.onSecondary
                                    else
                                        AppTheme.colors.caption,
                                fontSize = 26.sp,
                                modifier = Modifier.clickable {
                                    debtAmountOnClick(userInfo, debtAmount)
                                }
                            )
                        }
                    }
                )
            }
        }
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
                    usernameSearchQuery = usernameSearchQuery,
                    userInfos = userInfos,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun KeyPadBottomSheet(
    state: ModalBottomSheetState,
    initialMoneyAmount: Double,
    isEnabled: (Double) -> Boolean,
    onClick: (Double) -> Unit,
    onBackPress: () -> Unit,
    content: @Composable () -> Unit
) {
    var moneyAmount: String by remember { mutableStateOf("0") }
    LaunchedEffect(initialMoneyAmount) {
        moneyAmount = if (initialMoneyAmount % 1 == 0.0)
            initialMoneyAmount.roundToInt().toString()
        else
            ((initialMoneyAmount * 100).roundToInt() / 100.0).toString()
    }

    BackPressModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            KeyPadScreenLayout(
                moneyAmount = moneyAmount,
                onMoneyAmountChange = { moneyAmount = it },
                confirmButton = {
                    val actualMoneyAmount = moneyAmount.toDouble()
                    H1ConfirmTextButton(
                        text = "Confirm",
                        enabled = isEnabled(actualMoneyAmount),
                        onClick = {
                            onClick(actualMoneyAmount)
                            onBackPress()
                        }
                    )
                },
                onBackPress = onBackPress
            )
        },
        content = content
    )
}

@Composable
private fun SelectDebtorsChecklist(
    usernameSearchQuery: String,
    userInfos: List<UserInfo>,
    selectedUsers: Set<UserInfo>,
    onCheckedChange: (UserInfo, Boolean) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            userInfos.filter { userInfo ->
                userInfo.nickname!!.contains(usernameSearchQuery, ignoreCase = true)
            }
        ) { userInfo ->
            Row(
                horizontalArrangement = Arrangement.SpaceAround,
                modifier = Modifier.fillMaxWidth()
            ) {
                UserInfoRowCard(
                    userInfo = userInfo,
                    mainContent = {
                        H1Text(text = userInfo.nickname!!)
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
