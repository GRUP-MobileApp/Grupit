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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.apptheme.venmo
import com.grup.ui.compose.AcceptRejectRow
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.UserCaption
import com.grup.ui.compose.UserRowCard
import com.grup.ui.compose.asMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.compose.isoTime
import com.grup.ui.viewmodel.DebtActionDetailsViewModel

internal class DebtActionDetailsView(private val actionId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val debtActionDetailsViewModel = rememberScreenModel { DebtActionDetailsViewModel(actionId) }

        DebtActionDetailsLayout(
            debtActionDetailsViewModel = debtActionDetailsViewModel,
            navigator = navigator
        )
    }
}

@Composable
private fun DebtActionDetailsLayout(
    debtActionDetailsViewModel: DebtActionDetailsViewModel,
    navigator: Navigator
) {
    val debtAction: DebtAction by debtActionDetailsViewModel.debtAction.collectAsStateWithLifecycle()

    val (pendingTransactionRecords, completedTransactionRecords) =
        debtAction.transactionRecords.partition { it.status is TransactionRecord.Status.Pending }

    var selectedTransactionType: String by remember {
        mutableStateOf(if (pendingTransactionRecords.isNotEmpty()) "Pending" else "Completed")
    }

    if (selectedTransactionType == "Pending" && pendingTransactionRecords.isEmpty()) {
        selectedTransactionType = "Completed"
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
                    with(debtAction) {
                        UserRowCard(
                            user = userInfo.user,
                            mainContent = {
                                H1Text(
                                    text = userInfo.user.displayName,
                                    fontSize = AppTheme.typography.extraLargeFont,
                                    maxLines = 2
                                )
                                UserCaption(user = userInfo.user)
                            },
                            sideContent = {
                                Caption(
                                    text = "Debt Request",
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
                                Spacer(
                                    modifier = Modifier
                                        .height(AppTheme.dimensions.spacingExtraSmall)
                                )
                                with(debtAction.platform) {
                                    Caption(
                                        text = name,
                                        fontSize = AppTheme.typography.tinyFont,
                                        color = when(this) {
                                            DebtAction.Platform.Grupit -> AppTheme.colors.onPrimary
                                            DebtAction.Platform.Venmo -> venmo
                                        }
                                    )
                                }
                            },
                            iconSize = AppTheme.dimensions.largeIconSize
                        )
                    }
                }
                item {
                    MoneyAmount(
                        moneyAmount = debtAction.amount,
                        fontSize = AppTheme.typography.bigMoneyAmountFont
                    )
                }
                item {
                    H1Text(
                        text = "\"${debtAction.message}\"",
                        fontSize = AppTheme.typography.largeFont,
                        maxLines = 1,
                        modifier = Modifier
                            .padding(vertical = AppTheme.dimensions.spacingMedium)
                    )
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
                    }.sortedWith(
                        compareByDescending<TransactionRecord> {
                            it.userInfo.user.id != debtAction.userInfo.user.id
                        }.thenByDescending {
                            it.userInfo.user.id == debtActionDetailsViewModel.userId
                        }.thenByDescending {
                            it.status is TransactionRecord.Status.Accepted
                        }.thenByDescending {
                            it.dateCreated
                        }
                    )
                ) { transactionRecord ->
                    TransactionRecordRowCard(
                        transactionRecord = transactionRecord,
                        additionalMainContent = {
                            if (transactionRecord.status !is TransactionRecord.Status.Pending) {
                                Caption(
                                    text = with(transactionRecord.status) {
                                        status +
                                                if (this is TransactionRecord.Status.Accepted)
                                                    " on ${isoDate(date)}"
                                                else
                                                    ""
                                    }
                                )
                            }
                        },
                        moneyAmountTextColor = when (transactionRecord.status) {
                            is TransactionRecord.Status.Accepted ->
                                AppTheme.colors.confirm

                            is TransactionRecord.Status.Rejected -> AppTheme.colors.deny
                            else -> AppTheme.colors.onSecondary
                        },
                        modifier = Modifier
                            .clip(AppTheme.shapes.large)
                            .background(AppTheme.colors.secondary)
                            .padding(AppTheme.dimensions.rowCardPadding)
                    )
                }

                item {
                    if (
                        selectedTransactionType == "Pending" &&
                        pendingTransactionRecords.isNotEmpty()
                    ) {
                        H1Text(
                            text = "Total Pending: " +
                                    pendingTransactionRecords.sumOf {
                                        it.balanceChange
                                    }.asMoneyAmount()
                        )
                    } else if (
                        selectedTransactionType == "Completed" &&
                        completedTransactionRecords.isNotEmpty() &&
                        debtAction.acceptedAmount != debtAction.amount
                    ) {
                        H1Text(
                            text = "Total Completed: " +
                                    completedTransactionRecords.filter {
                                        it.status is TransactionRecord.Status.Accepted
                                    }.sumOf { it.balanceChange }.asMoneyAmount()
                        )
                    }
                }
            }
            debtAction.transactionRecords.find {
                it.userInfo.user.id == debtActionDetailsViewModel.userId
            }?.let { transactionRecord ->
                if (transactionRecord.status is TransactionRecord.Status.Pending) {
                    AcceptRejectRow(
                        acceptOnClick = {
                            debtActionDetailsViewModel.acceptDebtAction(
                                transactionRecord = transactionRecord,
                                onSuccess = {
                                    navigator.popUntil {
                                        it is GroupDetailsView
                                    }
                                },
                                onError = { }
                            )
                        },
                        rejectOnClick = {
                            debtActionDetailsViewModel.rejectDebtAction(
                                transactionRecord = transactionRecord,
                                onSuccess = {
                                    navigator.popUntil {
                                        it is GroupDetailsView
                                    }
                                },
                                onError = { }
                            )
                        },
                        modifier = Modifier.padding(bottom = AppTheme.dimensions.appPadding)
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionRecordRowCard(
    modifier: Modifier = Modifier,
    transactionRecord: TransactionRecord,
    additionalMainContent: @Composable () -> Unit = { },
    moneyAmountTextColor: Color = AppTheme.colors.onSecondary
) {
    UserRowCard(
        user = transactionRecord.userInfo.user,
        mainContent = {
            H1Text(text = transactionRecord.userInfo.user.displayName)
            UserCaption(user = transactionRecord.userInfo.user)
            additionalMainContent()
        },
        sideContent = {
            MoneyAmount(
                moneyAmount = transactionRecord.balanceChange,
                color = moneyAmountTextColor
            )
        },
        modifier = modifier
            .fillMaxWidth()
            .height(AppTheme.dimensions.itemRowCardHeight)
    )
}
