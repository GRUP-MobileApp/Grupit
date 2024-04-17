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
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1DenyTextButton
import com.grup.ui.compose.H1Header
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.TransactionRecordRowCard
import com.grup.ui.compose.UserRowCard
import com.grup.ui.compose.asMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.compose.isoTime
import com.grup.ui.viewmodel.DebtActionDetailsViewModel

internal class DebtActionDetailsView(private val debtActionId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val debtActionDetailsViewModel =
            rememberScreenModel { DebtActionDetailsViewModel(debtActionId) }

        CompositionLocalProvider(
            LocalContentColor provides AppTheme.colors.onSecondary
        ) {
            DebtActionDetailsLayout(
                debtActionDetailsViewModel = debtActionDetailsViewModel,
                navigator = navigator
            )
        }
    }
}

@Composable
private fun DebtActionDetailsLayout(
    debtActionDetailsViewModel: DebtActionDetailsViewModel,
    navigator: Navigator
) {
    val debtAction: DebtAction by
            debtActionDetailsViewModel.debtAction.collectAsStateWithLifecycle()

    fun isMyTransactionRecord(transactionRecord: TransactionRecord): Boolean =
        transactionRecord.userInfo.user.id == debtActionDetailsViewModel.userObject.id

    val (pendingTransactionRecords, completedTransactionRecords) =
        debtAction.transactionRecords.partition { it.status is TransactionRecord.Status.Pending }

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
                    with(debtAction) {
                        UserRowCard(
                            user = userInfo.user,
                            mainContent = {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Caption(text = "Debt Request")
                                        Caption(text = "@${userInfo.user.venmoUsername}")
                                        H1Text(
                                            text = userInfo.user.displayName,
                                            fontSize = 28.sp
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Caption(
                                            text = "${isoDate(date)} at ${isoTime(date)}",
                                            fontSize = AppTheme.typography.tinyFont
                                        )
                                        Caption(
                                            text = platform.name,
                                            fontSize = AppTheme.typography.tinyFont
                                        )
                                    }
                                }
                            },
                            iconSize = 80.dp
                        )
                    }
                }
                item { MoneyAmount(moneyAmount = debtAction.amount, fontSize = 60.sp) }
                item {
                    H1Text(
                        text = "\"${debtAction.message}\"",
                        modifier = Modifier.padding(vertical = AppTheme.dimensions.spacingMedium)
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
                    }.sortedWith (
                        compareBy<TransactionRecord> {
                            it.userInfo.user.id == debtActionDetailsViewModel.userObject.id
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
                            else ->
                                if (
                                    transactionRecord.userInfo.user.id ==
                                    debtActionDetailsViewModel.userObject.id
                                ) AppTheme.colors.deny
                                else AppTheme.colors.onSecondary
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
                debtAction.transactionRecords.find {
                    isMyTransactionRecord(it)
                }?.status is TransactionRecord.Status.Pending
            ) {
                H1DenyTextButton(
                    text = "Accept",
                    onClick = {
                        debtActionDetailsViewModel.acceptDebtAction(
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
    }
}
