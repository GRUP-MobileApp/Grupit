package com.grup.ui.compose.views

import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.SettleAction
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.KeyPadScreenLayout
import com.grup.ui.compose.asPureMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.viewmodel.SettleActionTransactionViewModel
import kotlin.math.abs
import kotlin.math.min

internal class SettleActionTransactionView(
    private val groupId: String,
    private val settleActionId: String
) : Screen {
    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        val settleActionTransactionViewModel =
            rememberScreenModel { SettleActionTransactionViewModel(groupId, settleActionId) }
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            SettleActionLayout(
                settleActionTransactionViewModel = settleActionTransactionViewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun SettleActionLayout(
    settleActionTransactionViewModel: SettleActionTransactionViewModel,
    navigator: Navigator
) {
    var settleActionTransactionAmount: String by remember { mutableStateOf("0") }

    val myUserInfo: UserInfo by
            settleActionTransactionViewModel.myUserInfo.collectAsStateWithLifecycle()
    val settleAction: SettleAction by
            settleActionTransactionViewModel.settleAction.collectAsStateWithLifecycle()

    val maxTransactionAmount: Double =
        min(abs(min(myUserInfo.userBalance, 0.0)), settleAction.remainingAmount)

    KeyPadScreenLayout(
        moneyAmount = settleActionTransactionAmount,
        onMoneyAmountChange = { moneyAmount ->
            settleActionTransactionAmount = if (moneyAmount.toDouble() > maxTransactionAmount) {
                maxTransactionAmount.asPureMoneyAmount()
            } else {
                moneyAmount
            }
        },
        onBackPress = { navigator.pop() },
        confirmButton = {
            H1ConfirmTextButton(
                text = "Settle",
                onClick = {
                    settleActionTransactionViewModel.createSettleActionTransaction(
                        amount = settleActionTransactionAmount.toDouble(),
                        onSuccess = {
                            navigator.popUntil { it is GroupDetailsView }
                        },
                        onError = { }
                    )
                }
            )
        }
    )
}
