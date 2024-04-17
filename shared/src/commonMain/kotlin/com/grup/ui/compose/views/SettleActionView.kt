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
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.KeyPadScreenLayout
import com.grup.ui.compose.asPureMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.viewmodel.SettleActionViewModel

internal class SettleActionView(private val groupId: String) : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val settleActionViewModel = rememberScreenModel { SettleActionViewModel(groupId) }
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            SettleActionLayout(settleActionViewModel = settleActionViewModel, navigator = navigator)
        }
    }
}

@Composable
private fun SettleActionLayout(
    settleActionViewModel: SettleActionViewModel,
    navigator: Navigator
) {
    var settleActionAmount: String by remember { mutableStateOf("0") }

    val myUserInfo: UserInfo by settleActionViewModel.myUserInfo.collectAsStateWithLifecycle()

    KeyPadScreenLayout(
        moneyAmount = settleActionAmount,
        onMoneyAmountChange = { moneyAmount ->
            settleActionAmount = if (moneyAmount.toDouble() > myUserInfo.userBalance) {
                myUserInfo.userBalance.asPureMoneyAmount()
            } else {
                moneyAmount
            }
        },
        onBackPress = { navigator.pop() },
        confirmButton = {
            H1ConfirmTextButton(
                text = "Settle",
                enabled = settleActionAmount.toDouble() != 0.0,
                onClick = {
                    settleActionViewModel.createSettleAction(
                        settleActionAmount.toDouble(),
                        onSuccess = {
                            navigator.pop()
                        },
                        onError = { }
                    )
                }
            )
        }
    )
}
