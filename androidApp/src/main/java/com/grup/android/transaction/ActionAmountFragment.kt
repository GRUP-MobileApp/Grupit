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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import com.grup.android.R
import com.grup.android.ui.*
import com.grup.android.ui.apptheme.AppTheme
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
                        actionType = requireArguments().getString("actionType")!!
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun ActionAmountLayout(
    transactionViewModel: TransactionViewModel,
    navController: NavController,
    actionType: String
) {
    val myUserInfo: UserInfo by transactionViewModel.myUserInfo.collectAsStateWithLifecycle()
    var actionAmount: String by remember { mutableStateOf("0") }
    var message: String by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            ActionAmountTopAppBar(
                onBackPress = { navController.popBackStack() }
            )
        },
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
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileIcon(
                        imageVector = Icons.Default.Face,
                        iconSize = 90.dp
                    )
                    Column(horizontalAlignment = Alignment.Start) {
                        caption(text = "Balance")
                        MoneyAmount(
                            moneyAmount = myUserInfo.userBalance,
                            fontSize = 48.sp
                        )
                    }
                }
                h1Text(
                    text = "$$actionAmount",
                    color = AppTheme.colors.onSecondary,
                    fontSize = 98.sp
                )
                Row(modifier = Modifier.height(40.dp)) {
                    if (actionType == TransactionViewModel.DEBT) {
                        TransparentTextField(
                            value = message,
                            onValueChange = { message = it }
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
                                actionAmount += key
                            }
                        }
                        '<' -> {
                            actionAmount = if (actionAmount.length > 1) {
                                actionAmount.substring(0, actionAmount.length - 1)
                            } else {
                                "0"
                            }
                        }
                        else -> {
                            if (key.isDigit() &&
                                (actionAmount.length < 3 ||
                                        actionAmount[actionAmount.length - 3] != '.')) {
                                if (actionAmount == "0") {
                                    actionAmount = ""
                                }
                                actionAmount += key
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
                if (actionType == TransactionViewModel.DEBT) {
                    RequestButton(
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
                } else if (actionType == TransactionViewModel.SETTLE) {
                    SettleButton(
                        onClick = {
                            transactionViewModel.createSettleAction(actionAmount.toDouble())
                            navController.popBackStack()
                        }
                    )
                }
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
        h1Text(
            text = key.toString(),
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AppTheme.colors.onSecondary
        )
    }
}

@Composable
fun RequestButton(
    onClick: () -> Unit
) {
    TextButton(
        colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
        modifier = Modifier
            .width(150.dp)
            .height(45.dp),
        shape = AppTheme.shapes.CircleShape,
        onClick = onClick
    ) {
        h1Text(
            text = "Request",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = AppTheme.colors.onSecondary,
        )
    }
}

@Composable
fun SettleButton(
    onClick: () -> Unit
) {
    TextButton(
        colors = ButtonDefaults.buttonColors(backgroundColor = AppTheme.colors.confirm),
        modifier = Modifier
            .width(150.dp)
            .height(45.dp),
        shape = AppTheme.shapes.CircleShape,
        onClick = onClick
    ) {
        h1Text(
            text = "Settle",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = AppTheme.colors.onSecondary,
        )
    }
}
