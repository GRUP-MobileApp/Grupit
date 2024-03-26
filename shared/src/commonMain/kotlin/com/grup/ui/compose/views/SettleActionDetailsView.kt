package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Header
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.TransactionRecordRowCard
import com.grup.ui.compose.UserRowCard
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.compose.isoTime
import com.grup.ui.viewmodel.SettleActionDetailsViewModel

internal class SettleActionDetailsView(
    private val groupId: String,
    private val settleActionId: String
) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val settleActionDetailsViewModel =
            rememberScreenModel { SettleActionDetailsViewModel(groupId, settleActionId) }

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            SettleActionDetailsLayout(
                settleActionDetailsViewModel = settleActionDetailsViewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun SettleActionDetailsLayout(
    settleActionDetailsViewModel: SettleActionDetailsViewModel,
    navigator: Navigator
) {
    val settleAction: SettleAction by
            settleActionDetailsViewModel.settleAction.collectAsStateWithLifecycle()

    val myUserInfo: UserInfo by
            settleActionDetailsViewModel.myUserInfo.collectAsStateWithLifecycle()

    fun isMyTransactionRecord(transactionRecord: TransactionRecord): Boolean =
        transactionRecord.userInfo.user.id == settleActionDetailsViewModel.userObject.id

    BackPressScaffold(
        onBackPress = { navigator.pop() }
    ) { padding ->
        Column(
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacing),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(AppTheme.dimensions.appPadding)
        ) {
            UserRowCard(
                user = settleAction.userInfo.user,
                mainContent = {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Caption(text = "Settle Request")
                        Caption(
                            text = "${isoDate(settleAction.date)} at ${isoTime(settleAction.date)}",
                            fontSize = AppTheme.typography.tinyFont
                        )
                    }
                    H1Text(text = settleAction.userInfo.user.displayName, fontSize = 28.sp)
                },
                iconSize = 80.dp
            )
            MoneyAmount(
                moneyAmount = with(settleAction) {
                    if (isCompleted) amount
                    else if (remainingAmount > 0) remainingAmount
                    else pendingAmount
                },
                fontSize = 60.sp
            )
            H1Header(text = "Transactions", modifier = Modifier.align(Alignment.Start))

            if (settleAction.transactionRecords.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(AppTheme.shapes.extraLarge)
                        .background(AppTheme.colors.secondary)
                ) {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.cardPadding),
                        contentPadding = PaddingValues(
                            horizontal = AppTheme.dimensions.rowCardPadding,
                            vertical = AppTheme.dimensions.cardPadding
                        )
                    ) {
                        items(
                            settleAction.transactionRecords.sortedByDescending {
                                isMyTransactionRecord(it)
                            }
                        ) { transactionRecord ->
                            TransactionRecordRowCard(
                                transactionRecord = transactionRecord,
                                moneyAmountTextColor = AppTheme.colors.confirm
                            )
                        }
                    }
                }
            }
            if (
                settleAction.userInfo.user.id != myUserInfo.user.id &&
                myUserInfo.userBalance < 0
            ) {
                Spacer(modifier = Modifier.weight(1f))
                H1ConfirmTextButton(
                    text = "Settle",
                    onClick = {
                        navigator.push(
                            SettleActionTransactionView(
                                settleActionDetailsViewModel.groupId,
                                settleActionDetailsViewModel.settleActionId
                            )
                        )
                    }
                )
            }
        }
    }
}
