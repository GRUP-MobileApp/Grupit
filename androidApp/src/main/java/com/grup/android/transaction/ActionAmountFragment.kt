package com.grup.android.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Face
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.R
import com.grup.android.getCurrencySymbol
import com.grup.android.ui.*
import com.grup.android.ui.apptheme.AppTheme
import com.grup.models.SettleAction
import com.grup.models.UserInfo

class ActionAmountFragment : Fragment() {
    private val transactionViewModel: TransactionViewModel by navGraphViewModels(R.id.main_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                CompositionLocalProvider(
                    LocalContentColor provides AppTheme.colors.onSecondary
                ) {
                    ActionAmountLayout(
                        transactionViewModel = transactionViewModel,
                        navController = findNavController(),
                        actionType = requireArguments().getString("actionType")!!,
                        existingActionId = requireArguments().getString("actionId")
                    )
                }
            }
        }
    }
}

@Composable
fun ActionAmountLayout(
    transactionViewModel: TransactionViewModel,
    navController: NavController,
    actionType: String,
    existingActionId: String?
) {
    val myUserInfo: UserInfo by transactionViewModel.myUserInfo.collectAsStateWithLifecycle()
    var actionAmount: String by remember { mutableStateOf("0") }

    val onBackPress: () -> Unit = { navController.popBackStack() }
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
                topContent = {
                    Row(modifier = Modifier.width(IntrinsicSize.Min)) {
                        UserInfoRowCard(
                            userInfo = myUserInfo,
                            iconSize = 90.dp,
                            mainContent = {
                                Caption(text = "Remaining Amount")
                                MoneyAmount(
                                    moneyAmount = myUserInfo.userBalance,
                                    fontSize = 48.sp
                                )
                            },
                            sideContent = null
                        )
                    }
                },
                actionButton = {
                    H1ConfirmTextButton(
                        text = actionType,
                        onClick = {
                            navController.navigate(
                                R.id.createDebtAction,
                                Bundle().apply {
                                    this.putDouble("amount", actionAmount.toDouble())
                                    this.putString("message", message)
                                }
                            )
                        }
                    )
                },
                onBackPress = onBackPress
            )
        }
        TransactionViewModel.SETTLE -> {
            // TODO: Check for positive user balance
            ActionAmountScreenLayout(
                actionAmount = actionAmount,
                onActionAmountChange = { onActionAmountChange(it, myUserInfo.userBalance) },
                topContent = {
                    Row(modifier = Modifier.width(IntrinsicSize.Min)) {
                        UserInfoRowCard(
                            userInfo = myUserInfo,
                            iconSize = 90.dp,
                            mainContent = {
                                Caption(text = "Remaining Amount")
                                MoneyAmount(
                                    moneyAmount = myUserInfo.userBalance,
                                    fontSize = 48.sp
                                )
                            },
                            sideContent = {}
                        )
                    }
                },
                actionButton = {
                    H1ConfirmTextButton(
                        text = actionType,
                        onClick = {
                            transactionViewModel.createSettleAction(actionAmount.toDouble())
                            onBackPress()
                        }
                    )
                },
                onBackPress = onBackPress
            )
        }
        TransactionViewModel.SETTLE_TRANSACTION -> {
            val settleAction: SettleAction by
            transactionViewModel.getSettleAction(existingActionId!!).collectAsStateWithLifecycle()
            ActionAmountScreenLayout(
                actionAmount = actionAmount,
                onActionAmountChange = { onActionAmountChange(it, settleAction.remainingAmount) },
                topContent = {
                    Row(modifier = Modifier.width(IntrinsicSize.Min)) {
                        UserInfoRowCard(
                            userInfo = settleAction.debteeUserInfo!!,
                            iconSize = 90.dp,
                            mainContent = {
                                Caption(text = "Remaining Amount")
                                MoneyAmount(
                                    moneyAmount = settleAction.remainingAmount,
                                    fontSize = 48.sp
                                )
                            },
                            sideContent = {}
                        )
                    }
                },
                actionButton = {
                    H1ConfirmTextButton(
                        text = actionType,
                        onClick = {
                            transactionViewModel.createSettleActionTransaction(
                                settleAction,
                                actionAmount.toDouble(),
                                myUserInfo
                            )
                            onBackPress()
                        }
                    )
                },
                onBackPress = onBackPress,
            )
        }
    }
}

@Composable
fun ActionAmountScreenLayout(
    actionAmount: String,
    onActionAmountChange: (String) -> Unit,
    message: String? = null,
    onMessageChange: ((String) -> Unit)? = null,
    topContent: @Composable () -> Unit,
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
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                topContent()
                H1Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = AppTheme.colors.onSecondary)) {
                            append(getCurrencySymbol())
                            append(actionAmount)
                        }
                        actionAmount.indexOf('.').let { index ->
                            if (index != -1) {
                                withStyle(SpanStyle(color = AppTheme.colors.caption)) {
                                    repeat(2 - (actionAmount.length - (index + 1))) {
                                        append('0')
                                    }
                                }
                            }
                        }
                    },
                    fontSize = 98.sp
                )
                Row(modifier = Modifier.height(40.dp)) {
                    message?.let { message ->
                        TransparentTextField(
                            value = message,
                            onValueChange = onMessageChange!!
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
                    .padding(top = 20.dp, bottom = 40.dp)
            ) {
                actionButton()
            }
        }
    }
}

@Composable
fun ActionAmountTopAppBar(
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
fun KeyPad(
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
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
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
fun Key(
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
            fontWeight = FontWeight.ExtraBold,
            color = AppTheme.colors.onSecondary
        )
    }
}
