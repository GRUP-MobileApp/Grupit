package com.grup.ui.compose.views

import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.KeyPadScreenLayout

internal class ActionAmountView : Screen {
    override val key: ScreenKey = uniqueScreenKey
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            ActionAmountLayout(navigator = navigator,)
        }
    }
}

@Composable
private fun ActionAmountLayout(
    navigator: Navigator,
) {
    var actionAmount: String by remember { mutableStateOf("0") }

    val onBackPress: () -> Unit = { navigator.pop() }
    fun onActionAmountChange(newActionAmount: String, maxAmount: Double = Double.MAX_VALUE)  {
        actionAmount = if (newActionAmount.toDouble() > maxAmount) {
            maxAmount.toString().trimEnd('0')
        } else {
            newActionAmount
        }
    }
    var message: String by remember { mutableStateOf("") }

    KeyPadScreenLayout(
        moneyAmount = actionAmount,
        onMoneyAmountChange = { onActionAmountChange(it) },
        message = message,
        onMessageChange = { message = it },
        confirmButton = {
            actionAmount.toDouble().let { amount ->
                H1ConfirmTextButton(
                    text = "Request",
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
