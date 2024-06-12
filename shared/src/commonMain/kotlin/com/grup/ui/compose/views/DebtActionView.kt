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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.apptheme.venmo
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Header
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.KeyPadScreenLayout
import com.grup.ui.compose.ModalBottomSheetLayout
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.SmallIcon
import com.grup.ui.compose.UserCaption
import com.grup.ui.compose.UserInfoAmountsList
import com.grup.ui.compose.UserInfoRowCard
import com.grup.ui.compose.UserRowCard
import com.grup.ui.compose.UsernameSearchBar
import com.grup.ui.compose.VenmoButton
import com.grup.ui.compose.asPureMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.viewmodel.DebtActionViewModel
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

internal class DebtActionView(private val groupId: String) : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val debtActionViewModel = rememberScreenModel { DebtActionViewModel(groupId) }

        DebtActionLayout(debtActionViewModel = debtActionViewModel, navigator = navigator)
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DebtActionLayout(debtActionViewModel: DebtActionViewModel, navigator: Navigator) {
    val keyboard = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val addDebtorBottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        confirmValueChange = {
            if (it == ModalBottomSheetValue.Hidden) {
                keyboard?.hide()
            }
            true
        }
    )

    var currentPage: Int by remember { mutableStateOf(0) }

    val userInfos: List<UserInfo> by debtActionViewModel.userInfos.collectAsStateWithLifecycle()

    var debtActionAmount: Double by remember { mutableStateOf(0.0) }
    var message: String by remember { mutableStateOf("") }

    var keyPadUserInfo: UserInfo? by remember { mutableStateOf(null) }

    var splitStrategy: DebtActionViewModel.SplitStrategy by remember {
        mutableStateOf(DebtActionViewModel.SplitStrategy.EvenSplit)
    }
    val rawSplitStrategyAmounts:
        SnapshotStateMap<UserInfo, Double?> = remember { mutableStateMapOf() }
    val debtActionAmounts: Map<UserInfo, Double> =
        splitStrategy.generateSplitStrategyAmounts(
            debtActionAmount,
            rawSplitStrategyAmounts
        )
    val isValid: Boolean =
        splitStrategy.isValid(debtActionAmount, debtActionAmounts) &&
                debtActionAmounts.keys.any { it.user.id != debtActionViewModel.userId }

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
                initialMoneyAmount = debtActionAmount,
                message = message,
                onMessageChange = { message = it },
                onBackPress = { navigator.pop() },
                changePageDebtActionDetails = { newDebtActionAmount ->
                    if (newDebtActionAmount != debtActionAmount) {
                        rawSplitStrategyAmounts.clear()
                    }
                    debtActionAmount = newDebtActionAmount
                    currentPage = 2
                }
            )

            1 -> keyPadUserInfo?.let { userInfo ->
                EditDebtorMoneyAmountKeypadPage(
                    initialMoneyAmount = rawSplitStrategyAmounts[userInfo] ?: 0.0,
                    debtActionMoneyAmount = debtActionAmount,
                    onClick = { debtAmount ->
                        rawSplitStrategyAmounts[userInfo] =
                            if (debtAmount > 0.0) debtAmount else null
                    },
                    onBackPress = { currentPage = 2 }
                )
            }

            2 -> AddDebtorBottomSheet(
                userInfos = userInfos.sortedBy { it.user.id != debtActionViewModel.userId },
                addDebtorsOnClick = { selectedUsers ->
                    rawSplitStrategyAmounts.keys.minus(selectedUsers).forEach { userInfo ->
                        rawSplitStrategyAmounts.remove(userInfo)
                    }
                    selectedUsers.minus(rawSplitStrategyAmounts.keys).forEach { userInfo ->
                        rawSplitStrategyAmounts[userInfo] = null
                    }
                    scope.launch { addDebtorBottomSheetState.hide() }
                },
                state = addDebtorBottomSheetState
            ) {
                BackPressScaffold(onBackPress = { currentPage = 0 }) { padding ->
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
                            fontSize = AppTheme.typography.keypadMoneyAmountFont
                        )
                        Column(
                            verticalArrangement = Arrangement
                                .spacedBy(AppTheme.dimensions.spacing),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                                .clip(AppTheme.shapes.large)
                                .background(AppTheme.colors.secondary)
                                .padding(horizontal = AppTheme.dimensions.cardPadding)
                                .padding(vertical = AppTheme.dimensions.cardPadding.times(0.7f))
                        ) {
                            DebtActionSettings(
                                splitStrategy = splitStrategy,
                                onSplitStrategyChange = { splitStrategy = it },
                                addDebtorsOnClick = {
                                    scope.launch { addDebtorBottomSheetState.show() }
                                }
                            )
                            UserInfoAmountsList(
                                userInfoMoneyAmounts = debtActionAmounts,
                                userInfoHasSetAmount = { userInfo ->
                                    if (!splitStrategy.editable) {
                                        true
                                    } else {
                                        rawSplitStrategyAmounts[userInfo] != null
                                    }
                                },
                                userInfoAmountOnClick = { userInfo ->
                                    if (splitStrategy.editable) {
                                        keyPadUserInfo = userInfo
                                        currentPage = 1
                                    }
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        H1Text(text = "Request with", fontWeight = FontWeight.SemiBold)
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            H1ConfirmTextButton(
                                text = "Grupit",
                                enabled = isValid,
                                onClick = {
                                    debtActionViewModel.createDebtAction(
                                        splitStrategy.generateMoneyAmounts(
                                            debtActionAmount,
                                            debtActionAmounts
                                        ),
                                        message
                                    ) { navigator.pop() }
                                }
                            )
                            H1ConfirmTextButton(
                                text = "Venmo",
                                enabled = isValid,
                                color = venmo,
                                onClick = { currentPage = 3 }
                            )
                        }
                    }
                }
            }

            3 -> userInfos.find { userInfo ->
                userInfo.user.id == debtActionViewModel.userId
            }?.let { userInfo ->
                DebtActionVenmoConfirmationPage(
                    debtActionAmount = debtActionAmount,
                    myUserInfo = userInfo,
                    debtActionAmounts = splitStrategy.generateMoneyAmounts(
                        debtActionAmount,
                        debtActionAmounts
                    ),
                    message = message,
                    onBackPress = { currentPage = 2 },
                    createDebtActionWithVenmo = {
                        debtActionViewModel.createDebtActionVenmo(
                            splitStrategy.generateMoneyAmounts(
                                debtActionAmount,
                                debtActionAmounts
                            ),
                            message
                        ) { navigator.pop() }
                    }
                )
            }
        }
    }

}

@Composable
private fun DebtActionKeypadPage(
    initialMoneyAmount: Double,
    message: String,
    onMessageChange: (String) -> Unit,
    onBackPress: () -> Unit,
    changePageDebtActionDetails: (Double) -> Unit
) {
    var debtActionAmount: String by remember { mutableStateOf("0") }
    LaunchedEffect(initialMoneyAmount) {
        debtActionAmount = if (initialMoneyAmount % 1 == 0.0)
            initialMoneyAmount.roundToInt().toString()
        else
            initialMoneyAmount.asPureMoneyAmount()
    }

    KeyPadScreenLayout(
        moneyAmount = debtActionAmount,
        onMoneyAmountChange = { newMoneyAmount ->
            debtActionAmount =
                if (newMoneyAmount.toDouble() > 999999999) {
                    999999999.toString()
                } else {
                    newMoneyAmount
                }
        },
        message = message,
        onMessageChange = { onMessageChange(it) },
        confirmButton = {
            H1ConfirmTextButton(
                text = "Next",
                enabled = debtActionAmount.toDouble() > 0 && message.isNotBlank(),
                onClick = { changePageDebtActionDetails(debtActionAmount.toDouble()) }
            )
        },
        onBackPress = onBackPress
    )
}

@Composable
private fun EditDebtorMoneyAmountKeypadPage(
    initialMoneyAmount: Double,
    debtActionMoneyAmount: Double,
    onClick: (Double) -> Unit,
    onBackPress: () -> Unit
) {
    var moneyAmount: String by remember { mutableStateOf("0") }
    LaunchedEffect(initialMoneyAmount) {
        moneyAmount = if (initialMoneyAmount % 1 == 0.0)
            initialMoneyAmount.roundToInt().toString()
        else
            initialMoneyAmount.asPureMoneyAmount()
    }

    KeyPadScreenLayout(
        moneyAmount = moneyAmount,
        onMoneyAmountChange = { newActionAmount ->
            moneyAmount = if (newActionAmount.toDouble() > debtActionMoneyAmount) {
                debtActionMoneyAmount.asPureMoneyAmount()
            } else {
                newActionAmount
            }
        },
        confirmButton = {
            H1ConfirmTextButton(
                text = "Confirm",
                onClick = {
                    onClick(moneyAmount.toDouble())
                    onBackPress()
                }
            )
        },
        onBackPress = onBackPress
    )
}

@Composable
private fun DebtActionSettings(
    splitStrategy: DebtActionViewModel.SplitStrategy,
    onSplitStrategyChange: (DebtActionViewModel.SplitStrategy) -> Unit,
    addDebtorsOnClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        H1Text(text = "by ")
        Box(
            modifier = Modifier
                .clip(AppTheme.shapes.medium)
                .background(AppTheme.colors.primary)
                .clickable {
                    // TODO: Make actual split strategy selection screen
                    when(splitStrategy) {
                        is DebtActionViewModel.SplitStrategy.EvenSplit -> {
                            onSplitStrategyChange(
                                DebtActionViewModel.SplitStrategy.UnevenSplit
                            )
                        }
                        is DebtActionViewModel.SplitStrategy.UnevenSplit -> {
                            onSplitStrategyChange(DebtActionViewModel.SplitStrategy.EvenSplit)
                        }
                        else -> {}
                    }
                }
                .padding(AppTheme.dimensions.spacingSmall)
        ) {
            H1Text(text = splitStrategy.name)
        }
        H1Text(text = " between:")
        Spacer(modifier = Modifier.weight(1f))
        SmallIcon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add a debtor",
            modifier = Modifier.clickable(onClick = addDebtorsOnClick)
        )
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

    ModalBottomSheetLayout(
        sheetState = state,
        sheetContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.appPadding)
            ) {
                H1Header(text = "Select Debtors", color = textColor)
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
                    H1ConfirmTextButton(
                        text = "Select",
                        width = AppTheme.dimensions.textButtonWidth.times(0.7f)
                    ) { addDebtorsOnClick(selectedUsers) }
                }
                SelectDebtorsChecklist(
                    userInfos = userInfos.filter { userInfo ->
                        userInfo.user.displayName.contains(usernameSearchQuery, ignoreCase = true)
                    },
                    selectedUsers = selectedUsers,
                    onCheckedChange = { userInfo ->
                        if (selectedUsers.contains(userInfo)) {
                            selectedUsers -= userInfo
                        } else {
                            selectedUsers += userInfo
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
    onCheckedChange: (UserInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.appPadding),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        items(userInfos) { userInfo ->
            val isSelected = selectedUsers.contains(userInfo)
            UserInfoRowCard(
                userInfo = userInfo,
                coloredMoneyAmount = !isSelected,
                modifier = Modifier
                    .clip(AppTheme.shapes.large)
                    .background(
                        if (isSelected) AppTheme.colors.confirm
                        else AppTheme.colors.primary
                    )
                    .clickable { onCheckedChange(userInfo) }
                    .padding(AppTheme.dimensions.rowCardPadding)
                    .height(AppTheme.dimensions.itemRowCardHeight)
            )
        }
    }
}

@Composable
private fun DebtActionVenmoConfirmationPage(
    debtActionAmount: Double,
    myUserInfo: UserInfo,
    debtActionAmounts: Map<UserInfo, Double>,
    message: String,
    onBackPress: () -> Unit,
    createDebtActionWithVenmo: (Map<UserInfo, Double>) -> Unit
) {
    BackPressScaffold(onBackPress = onBackPress) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(padding)
            ) {
                item {
                    UserRowCard(
                        user = myUserInfo.user,
                        mainContent = {
                            H1Text(
                                text = myUserInfo.user.displayName,
                                fontSize = AppTheme.typography.extraLargeFont,
                                maxLines = 2
                            )
                            UserCaption(user = myUserInfo.user)
                        },
                        sideContent = {
                            Caption(
                                text = "Debt Request",
                                fontSize = AppTheme.typography.tinyFont
                            )
                            Spacer(
                                modifier = Modifier
                                    .height(AppTheme.dimensions.spacingExtraSmall)
                            )
                            Caption(
                                text = "Venmo",
                                color = venmo,
                                fontSize = AppTheme.typography.tinyFont
                            )
                        },
                        iconSize = AppTheme.dimensions.largeIconSize
                    )
                }
                item {
                    MoneyAmount(
                        moneyAmount = debtActionAmount,
                        fontSize = AppTheme.typography.bigMoneyAmountFont
                    )
                }
                item {
                    H1Text(
                        text = "\"$message\"",
                        fontSize = AppTheme.typography.largeFont,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(vertical = AppTheme.dimensions.spacingMedium)
                    )
                }
                item {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        H1Text(
                            text = "Transactions",
                            fontSize = AppTheme.typography.mediumFont
                        )
                    }
                }
                items(
                    debtActionAmounts.toList().sortedByDescending { (userInfo, _) ->
                        userInfo.user.id == myUserInfo.user.id
                    }
                ) { (userInfo, balanceChange) ->
                    UserRowCard(
                        user = userInfo.user,
                        mainContent = {
                            H1Text(text = userInfo.user.displayName)
                            UserCaption(user = userInfo.user)
                        },
                        sideContent = {
                            Row(
                                horizontalArrangement =
                                    Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (userInfo.user.id != myUserInfo.user.id) {
                                    userInfo.user.venmoUsername?.let { venmoUsername ->
                                        VenmoButton(
                                            venmoUsername = venmoUsername,
                                            amount = balanceChange,
                                            note = message,
                                            isRequest = true
                                        )
                                    }
                                }
                                MoneyAmount(
                                    moneyAmount = balanceChange,
                                    color = AppTheme.colors.onSecondary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.dimensions.itemRowCardHeight)
                    )
                }
            }
            H1ConfirmTextButton(
                text = "Request",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = AppTheme.dimensions.appPadding)
            ) {
                createDebtActionWithVenmo(debtActionAmounts)
            }
        }
    }
}
