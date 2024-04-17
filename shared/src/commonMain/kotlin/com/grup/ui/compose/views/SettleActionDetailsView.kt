package com.grup.ui.compose.views

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.grup.ui.compose.asMoneyAmount
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

    val (pendingTransactionRecords, completedTransactionRecords) =
        settleAction.transactionRecords.partition { it.status is TransactionRecord.Status.Pending }

    var selectedTransactionType: String by remember {
        mutableStateOf(if (pendingTransactionRecords.isNotEmpty()) "Pending" else "Completed")
    }

    fun isMyTransactionRecord(transactionRecord: TransactionRecord): Boolean =
        transactionRecord.userInfo.user.id == settleActionDetailsViewModel.userObject.id

    BackPressScaffold(onBackPress = { navigator.pop() }) { padding ->
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                horizontalAlignment = Alignment.CenterHorizontally,
                contentPadding = PaddingValues(AppTheme.dimensions.appPadding),
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(padding)
            ) {
                item {
                    with(settleAction) {
                        UserRowCard(
                            user = userInfo.user,
                            mainContent = {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Caption(text = "Settle Request")
                                    Caption(
                                        text = "${isoDate(date)} at ${isoTime(date)}",
                                        fontSize = AppTheme.typography.tinyFont
                                    )
                                }
                                Caption(text = "@${userInfo.user.venmoUsername}")
                                H1Text(
                                    text = userInfo.user.displayName,
                                    fontSize = 28.sp
                                )
                            },
                            iconSize = 80.dp
                        )
                    }
                }
                item {
                    MoneyAmount(
                        moneyAmount = with(settleAction) {
                            if (isCompleted) amount
                            else if (remainingAmount > 0) remainingAmount
                            else pendingAmount
                        },
                        fontSize = 60.sp
                    )
                }
                item {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (pendingTransactionRecords.isNotEmpty()) {
                            H1Header(
                                text = "Pending",
                                fontSize =
                                    if (selectedTransactionType == "Pending")
                                        AppTheme.typography.headerFont.times(1.2f)
                                    else
                                        AppTheme.typography.headerFont,
                                modifier = Modifier.clickable {
                                    selectedTransactionType = "Pending"
                                }
                            )

                        }
                        if (completedTransactionRecords.isNotEmpty()) {
                            H1Header(
                                text = "Completed",
                                fontSize =
                                    if (selectedTransactionType == "Completed")
                                        AppTheme.typography.headerFont.times(1.2f)
                                    else
                                        AppTheme.typography.headerFont,
                                modifier = Modifier.clickable {
                                    selectedTransactionType = "Completed"
                                }
                            )
                        }
                    }
                }

                items(
                    if (selectedTransactionType == "Pending") {
                        pendingTransactionRecords
                    } else {
                        completedTransactionRecords
                    }.sortedBy { isMyTransactionRecord(it) }
                ) { transactionRecord ->
                    TransactionRecordRowCard(
                        transactionRecord = transactionRecord,
                        moneyAmountTextColor = when(transactionRecord.status) {
                            is TransactionRecord.Status.Accepted -> AppTheme.colors.confirm
                            is TransactionRecord.Status.Rejected -> AppTheme.colors.deny
                            else -> AppTheme.colors.onSecondary
                        },
                        modifier = Modifier.fillMaxWidth()
                            .clip(AppTheme.shapes.large)
                            .background(AppTheme.colors.secondary)
                            .padding(AppTheme.dimensions.rowCardPadding)
                    )
                }

                item {
                    if (selectedTransactionType == "Pending") {
                        H1Text(
                            text = "Total Pending: " +
                                    pendingTransactionRecords.sumOf {
                                        it.balanceChange
                                    }.asMoneyAmount()
                        )
                    } else {
                        H1Text(
                            text = "Total Accepted: " +
                                    completedTransactionRecords.filter {
                                        it.status is TransactionRecord.Status.Accepted
                                    }.sumOf { it.balanceChange }.asMoneyAmount()
                        )
                    }
                }
            }
            if (
                settleAction.userInfo.user.id != myUserInfo.user.id &&
                myUserInfo.userBalance < 0
            ) {
                H1ConfirmTextButton(
                    text = "Settle",
                    onClick = {
                        navigator.push(
                            SettleActionTransactionView(
                                settleActionDetailsViewModel.groupId,
                                settleActionDetailsViewModel.settleActionId
                            )
                        )
                    },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                        .padding(bottom = AppTheme.dimensions.appPadding)
                )
            }
        }
    }
}
