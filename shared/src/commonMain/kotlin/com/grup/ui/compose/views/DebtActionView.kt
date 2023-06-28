package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
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
    val scope = rememberCoroutineScope()

    val userInfos: List<UserInfo> by transactionViewModel.userInfos.collectAsStateWithLifecycle()
    var splitStrategy: TransactionViewModel.SplitStrategy by remember {
        mutableStateOf(TransactionViewModel.SplitStrategy.EvenSplit)
    }

    val rawSplitStrategyDebtAmounts:
            SnapshotStateMap<UserInfo, Double?> = remember { mutableStateMapOf() }
    val splitStrategyDebtAmounts: Map<UserInfo, Double> =
        splitStrategy.generateSplit(debtActionAmount, rawSplitStrategyDebtAmounts)

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
                ) {
                    Column(
                        verticalArrangement = Arrangement
                            .spacedBy(AppTheme.dimensions.spacing),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = AppTheme.dimensions.cardPadding)
                            .padding(horizontal = AppTheme.dimensions.cardPadding)
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
                                rawSplitStrategyDebtAmounts[userInfo] != null
                            },
                            setUserDebtAmount = if (splitStrategy.editable) {
                                { userInfo, amount ->
                                    rawSplitStrategyDebtAmounts[userInfo] = amount
                                }
                            } else null,
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
                    .padding(vertical = AppTheme.dimensions.spacing)
                    .clip(AppTheme.shapes.medium)
                    .background(AppTheme.colors.primary)
                    .clickable {
                        onSplitStrategyChange(TransactionViewModel.SplitStrategy.UnevenSplit)
                    }
            ) {
                H1Text(
                    text = splitStrategy.name,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(AppTheme.dimensions.spacingSmall)
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
    setUserDebtAmount: ((UserInfo, Double) -> Unit)?,
    modifier: Modifier = Modifier
) {
    // Keeps track of any values user is currently typing
    val proxyDebtAmounts: SnapshotStateMap<UserInfo, Double> = remember { mutableStateMapOf() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            items(splitStrategyDebtAmounts.keys.toTypedArray()) { userInfo ->
                UserInfoRowCard(
                    userInfo = userInfo,
                    sideContent = {
                        val hasEditedAmount =
                            setUserDebtAmount != null && userHasSetDebtAmount(userInfo)
                        var isFocused: Boolean by remember { mutableStateOf(false) }
                        setUserDebtAmount?.let { setUserDebtAmount ->
                            TextField(
                                value =
                                if (isFocused && proxyDebtAmounts[userInfo] == null) {
                                    ""
                                } else {
                                    (proxyDebtAmounts[userInfo]
                                        ?: splitStrategyDebtAmounts[userInfo]!!)
                                        .asMoneyAmount()
                                },
                                onValueChange = { text ->
                                    proxyDebtAmounts[userInfo] = text.replaceFirst(
                                        Regex("^\\p{Alpha}+"), ""
                                    ).toDouble()
                                },
                                textStyle = TextStyle(
                                    color =
                                    if (hasEditedAmount || proxyDebtAmounts.containsKey(userInfo))
                                        AppTheme.colors.onSecondary
                                    else
                                        AppTheme.colors.caption
                                ),
                                keyboardOptions = KeyboardOptions(
                                    keyboardType = KeyboardType.Decimal
                                ),
                                modifier = Modifier.onFocusChanged { focusState ->
                                    isFocused = focusState.isFocused
                                    if (!focusState.isFocused) {
                                        proxyDebtAmounts.forEach { entry ->
                                            setUserDebtAmount(entry.key, entry.value)
                                        }
                                        proxyDebtAmounts.clear()
                                    }
                                }
                            )
                        } ?: Text(
                            text = "pays ${splitStrategyDebtAmounts[userInfo]!!.asMoneyAmount()}",
                            color =
                            if (hasEditedAmount)
                                AppTheme.colors.onSecondary
                            else
                                AppTheme.colors.caption,
                            modifier = Modifier.apply {
                                onFocusChanged {
                                    // TODO: Update rawSplitStrategyDebtAmounts
                                }
                            }
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun AddDebtorBottomSheet(
    userInfos: List<UserInfo>,
    addDebtorsOnClick: (Set<UserInfo>) -> Unit,
    state: ModalBottomSheetState,
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
