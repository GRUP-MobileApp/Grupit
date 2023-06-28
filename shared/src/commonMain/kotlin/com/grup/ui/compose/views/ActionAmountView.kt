package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.SettleAction
import com.grup.models.UserInfo
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.getCurrencySymbol
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.*
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.TransparentTextField
import com.grup.ui.viewmodel.TransactionViewModel
import kotlin.math.min

internal class ActionAmountScreen(
    private val actionType: String,
    private val existingActionId: String? = null
) : Screen {
    @Composable
    override fun Content() {
        val transactionViewModel: TransactionViewModel =
            rememberScreenModel { TransactionViewModel() }
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            ActionAmountLayout(
                transactionViewModel = transactionViewModel,
                navigator = navigator,
                actionType = actionType,
                existingActionId = existingActionId
            )
        }
    }

}

@Composable
private fun ActionAmountLayout(
    transactionViewModel: TransactionViewModel,
    navigator: Navigator,
    actionType: String,
    existingActionId: String?
) {
    val myUserInfo: UserInfo by transactionViewModel.myUserInfo.collectAsStateWithLifecycle()
    var actionAmount: String by remember { mutableStateOf("0") }

    val onBackPress: () -> Unit = { navigator.pop() }
    fun onActionAmountChange(newActionAmount: String, maxAmount: Double = Double.MAX_VALUE)  {
        actionAmount = if (newActionAmount.toDouble() > maxAmount) {
            maxAmount.toString().trimEnd('0')
        } else {
            newActionAmount
        }
    }

    when (actionType) {
        TransactionViewModel.DEBT -> {
            var message: String by remember { mutableStateOf("") }

            ActionAmountScreenLayout(
                actionAmount = actionAmount,
                onActionAmountChange = { onActionAmountChange(it) },
                message = message,
                onMessageChange = { message = it },
                actionButton = {
                    actionAmount.toDouble().let { amount ->
                        H1ConfirmTextButton(
                            text = actionType,
                            enabled = amount > 0 && message.isNotBlank(),
                            onClick = {
                                navigator.push(
                                    DebtActionView(
                                        debtActionAmount = amount,
                                        message = message
                                    )
                                )
                            }
                        )
                    }
                },
                onBackPress = onBackPress
            )
        }
        TransactionViewModel.SETTLE -> {
            ActionAmountScreenLayout(
                actionAmount = actionAmount,
                onActionAmountChange = { onActionAmountChange(it, myUserInfo.userBalance) },
                actionButton = {
                    actionAmount.toDouble().let { amount ->
                        H1ConfirmTextButton(
                            text = actionType,
                            enabled = amount > 0,
                            onClick = {
                                transactionViewModel.createSettleAction(amount)
                                onBackPress()
                            }
                        )
                    }
                },
                onBackPress = onBackPress
            )
        }
        TransactionViewModel.SETTLE_TRANSACTION -> {
            val settleAction: SettleAction by
            transactionViewModel.getSettleAction(existingActionId!!).collectAsStateWithLifecycle()
            ActionAmountScreenLayout(
                actionAmount = actionAmount,
                onActionAmountChange = {
                    onActionAmountChange(
                        it,
                        min(settleAction.remainingAmount, -1 * myUserInfo.userBalance)
                    )
                },
                actionButton = {
                    actionAmount.toDouble().let { amount ->
                        H1ConfirmTextButton(
                            text = actionType,
                            enabled = amount > 0,
                            onClick = {
                                transactionViewModel.createSettleActionTransaction(
                                    settleAction,
                                    amount,
                                    myUserInfo
                                )
                                onBackPress()
                            }
                        )
                    }
                },
                onBackPress = onBackPress,
            )
        }
    }
}

@Composable
private fun ActionAmountScreenLayout(
    actionAmount: String,
    actionAmountFontSize: TextUnit = 98.sp,
    onActionAmountChange: (String) -> Unit,
    message: String? = null,
    onMessageChange: ((String) -> Unit)? = null,
    actionButton: @Composable () -> Unit,
    onBackPress: () -> Unit,
) {
    Scaffold(
        topBar = { ActionAmountTopAppBar(onBackPress = onBackPress) },
        backgroundColor = AppTheme.colors.primary
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.dimensions.appPadding)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                AutoSizingH1Text(
                    textContent = { fontSize ->
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = AppTheme.colors.onSecondary)) {
                                withStyle(
                                    SpanStyle(
                                        fontSize = fontSize.times(0.5f),
                                        baselineShift = BaselineShift(0.4f)
                                    )
                                ) {
                                    append(getCurrencySymbol())
                                }
                                withStyle(SpanStyle(fontSize = fontSize)) {
                                    append(actionAmount)
                                }
                            }
                            actionAmount.indexOf('.').let { index ->
                                if (index != -1) {
                                    withStyle(
                                        SpanStyle(
                                            color = AppTheme.colors.caption,
                                            fontSize = fontSize
                                        )
                                    ) {
                                        repeat(2 - (actionAmount.length - (index + 1))) {
                                            append('0')
                                        }
                                    }
                                }
                            }
                        }
                    },
                    fontSize = actionAmountFontSize,
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .height(65.dp)
                        .padding(horizontal = AppTheme.dimensions.paddingSmall)
                ) {
                    message?.let { message ->
                        if (message.isEmpty()) {
                            Caption(text = "What is this for?", fontSize = 24.sp)
                        }
                        TransparentTextField(
                            value = message,
                            onValueChange = {
                                onMessageChange!!(it.take(50))
                            },
                            fontSize = 24.sp
                        )
                    }
                }
            }
            KeyPad(
                modifier = Modifier.padding(top = AppTheme.dimensions.spacing),
                onKeyPress = { key ->
                    when(key) {
                        '.' -> {
                            if (!actionAmount.contains('.')) {
                                onActionAmountChange(actionAmount + key)
                            }
                        }
                        '<' -> {
                            onActionAmountChange(
                                if (actionAmount.length > 1) {
                                    actionAmount.substring(0, actionAmount.length - 1)
                                } else {
                                    "0"
                                }
                            )
                        }
                        else -> {
                            if (key.isDigit() &&
                                (actionAmount.length < 3 ||
                                        actionAmount[actionAmount.length - 3] != '.')) {
                                onActionAmountChange(
                                    if (actionAmount == "0") {
                                        key.toString()
                                    } else {
                                        actionAmount + key
                                    }
                                )
                            }
                        }
                    }
                }
            )
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = AppTheme.dimensions.cardPadding)
            ) {
                actionButton()
            }
        }
    }
}

@Composable
private fun ActionAmountTopAppBar(
    onBackPress: () -> Unit
) {
    TopAppBar(
        title = {},
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
private fun KeyPad(
    modifier: Modifier = Modifier,
    onKeyPress: (Char) -> Unit
) {
    val keys: List<List<Char>> = listOf(
        listOf('1', '2', '3'),
        listOf('4', '5', '6'),
        listOf('7', '8', '9'),
        listOf('.', '0', '<')
    )
    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxHeight(0.4f)
            .fillMaxWidth()
    ) {
        keys.forEach { row ->
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.fillMaxWidth()
            ) {
                row.forEach { key ->
                    Key(key = key, onKeyPress = onKeyPress)
                }
            }
        }
    }
}

@Composable
private fun Key(
    key: Char,
    onKeyPress: (Char) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .height(50.dp)
            .background(AppTheme.colors.primary)
            .clickable { onKeyPress(key) }
    ) {
        H1Text(
            text = key.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.Black,
            color = AppTheme.colors.onSecondary
        )
    }
}
