package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.Action
import com.grup.models.DebtAction
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.TransactionRecordRowCard
import com.grup.ui.compose.UserRowCard
import com.grup.ui.compose.asMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.compose.isoTime
import com.grup.ui.viewmodel.ActionDetailsViewModel

internal class ActionDetailsView(private val actionId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val actionDetailsViewModel =
            rememberScreenModel { ActionDetailsViewModel(actionId) }

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            ActionDetailsLayout(
                actionDetailsViewModel = actionDetailsViewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun ActionDetailsLayout(
    actionDetailsViewModel: ActionDetailsViewModel,
    navigator: Navigator
) {
    val action: Action by actionDetailsViewModel.action.collectAsStateWithLifecycle()

    val myUserInfo: UserInfo by
        actionDetailsViewModel.myUserInfo.collectAsStateWithLifecycle()

    fun isMyTransactionRecord(transactionRecord: TransactionRecord): Boolean =
        transactionRecord.userInfo.user.id == actionDetailsViewModel.userObject.id

    val (pendingTransactionRecords, completedTransactionRecords) =
        action.transactionRecords.partition { it.status is TransactionRecord.Status.Pending }

    var selectedTransactionType: String by remember {
        mutableStateOf(if (pendingTransactionRecords.isNotEmpty()) "Pending" else "Completed")
    }

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
                    with(action) {
                        UserRowCard(
                            user = userInfo.user,
                            mainContent = {
                                H1Text(
                                    text = userInfo.user.displayName,
                                    fontSize = AppTheme.typography.extraLargeFont,
                                    maxLines = 2
                                )
                                Caption(text = "@${userInfo.user.venmoUsername}")
                            },
                            sideContent = {
                                Caption(
                                    text = when(action) {
                                        is DebtAction -> "Debt Request"
                                        is SettleAction -> "Settle Request"
                                    },
                                    fontSize = AppTheme.typography.tinyFont
                                )
                                Spacer(
                                    modifier = Modifier
                                        .height(AppTheme.dimensions.spacingExtraSmall)
                                )
                                Caption(
                                    text = "${isoDate(date)} at ${isoTime(date)}",
                                    fontSize = AppTheme.typography.tinyFont
                                )
                                (action as? DebtAction)?.let { debtAction ->
                                    Spacer(
                                        modifier = Modifier
                                            .height(AppTheme.dimensions.spacingExtraSmall)
                                    )
                                    Caption(
                                        text = debtAction.platform.name,
                                        fontSize = AppTheme.typography.tinyFont
                                    )
                                }
                            },
                            iconSize = AppTheme.dimensions.largeIconSize
                        )
                    }
                }
                item {
                    MoneyAmount(
                        moneyAmount = when(action) {
                            is DebtAction -> action.amount
                            is SettleAction -> with(action as SettleAction) {
                                if (isCompleted) amount
                                else if (remainingAmount > 0) remainingAmount
                                else pendingAmount
                            }
                        },
                        fontSize = AppTheme.typography.bigMoneyAmountFont
                    )
                }
                (action as? DebtAction)?.let { debtAction ->
                    item {
                        H1Text(
                            text = "\"${debtAction.message}\"",
                            fontSize = AppTheme.typography.largeFont,
                            maxLines = 1,
                            modifier = Modifier
                                .padding(vertical = AppTheme.dimensions.spacingMedium)
                        )
                    }
                }
                item {
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (pendingTransactionRecords.isNotEmpty()) {
                            H1Text(
                                text = "Pending",
                                fontSize =
                                    if (selectedTransactionType == "Pending")
                                        AppTheme.typography.mediumFont.times(1.2f)
                                    else
                                        AppTheme.typography.mediumFont,
                                modifier = Modifier.clickable {
                                    selectedTransactionType = "Pending"
                                }
                            )

                        }
                        if (completedTransactionRecords.isNotEmpty()) {
                            H1Text(
                                text = "Completed",
                                fontSize =
                                    if (selectedTransactionType == "Completed")
                                        AppTheme.typography.mediumFont.times(1.2f)
                                    else
                                        AppTheme.typography.mediumFont,
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
                    }.sortedWith (
                        compareBy<TransactionRecord> {
                            it.userInfo.user.id == actionDetailsViewModel.userObject.id
                        }.thenBy {
                            it.status !is TransactionRecord.Status.Rejected
                        }
                    )
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
            when(action) {
                is DebtAction -> with(action as DebtAction) {
                    if (
                        transactionRecords.find {
                            isMyTransactionRecord(it)
                        }?.status is TransactionRecord.Status.Pending
                    ) {
                        H1ConfirmTextButton(
                            text = "Accept",
                            onClick = {
                                actionDetailsViewModel.acceptDebtAction(
                                    debtAction = this,
                                    onSuccess = {
                                        navigator.popUntil {
                                            it is GroupDetailsView
                                        }
                                    },
                                    onError = { }
                                )
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(bottom = AppTheme.dimensions.appPadding)
                        )
                    }
                }
                is SettleAction -> with(action as SettleAction) {
                    if (
                        userInfo.user.id != myUserInfo.user.id &&
                        myUserInfo.userBalance < 0
                    ) {
                        H1ConfirmTextButton(
                            text = "Settle",
                            onClick = {
                                navigator.push(
                                    SettleActionTransactionView(userInfo.group.id, id)
                                )
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(bottom = AppTheme.dimensions.appPadding)
                        )
                    }
                }
            }
        }
    }
}
