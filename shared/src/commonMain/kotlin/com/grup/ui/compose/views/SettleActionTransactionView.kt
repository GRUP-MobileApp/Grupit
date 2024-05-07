package com.grup.ui.compose.views

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentColor
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.SettleAction
import com.grup.models.UserInfo
import com.grup.other.Platform
import com.grup.other.platform
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.KeyPadScreenLayout
import com.grup.ui.compose.ModalBottomSheetLayout
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.UserRowCard
import com.grup.ui.compose.VenmoButton
import com.grup.ui.compose.asPureMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.viewmodel.SettleActionTransactionViewModel
import kotlinx.coroutines.launch
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun SettleActionLayout(
    settleActionTransactionViewModel: SettleActionTransactionViewModel,
    navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val venmoConfirmationBottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    var settleActionTransactionAmount: String by remember { mutableStateOf("0") }

    val myUserInfo: UserInfo by
            settleActionTransactionViewModel.myUserInfo.collectAsStateWithLifecycle()
    val settleAction: SettleAction by
            settleActionTransactionViewModel.settleAction.collectAsStateWithLifecycle()

    val maxTransactionAmount: Double =
        min(abs(min(myUserInfo.userBalance, 0.0)), settleAction.remainingAmount)

    ModalBottomSheetLayout(
        sheetState = venmoConfirmationBottomSheetState,
        sheetContent = {
            Column(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimensions.appPadding)
            ) {
                H1Text(text = "Settle Transaction")
                with(settleAction.userInfo) {
                    UserRowCard(
                        user = user,
                        mainContent = {
                            H1Text(text = user.displayName)
                            Caption(text = "@${user.venmoUsername}")
                        },
                        sideContent = {
                            Row(
                                horizontalArrangement =
                                Arrangement.spacedBy(AppTheme.dimensions.spacingMedium),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                VenmoButton(
                                    venmoUsername = user.venmoUsername,
                                    amount = settleActionTransactionAmount.toDouble(),
                                    note = "Grupit Settlement",
                                    isRequest = true
                                )
                                MoneyAmount(
                                    moneyAmount = settleActionTransactionAmount.toDouble(),
                                    color = AppTheme.colors.onSecondary
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AppTheme.dimensions.itemRowCardHeight)
                    )
                    H1ConfirmTextButton(
                        text = "Confirm",
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
            }
        }
    ) {
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
                    onClick = { scope.launch { venmoConfirmationBottomSheetState.show() } }
                )
            }
        )
    }
}
