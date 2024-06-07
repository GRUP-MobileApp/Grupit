package com.grup.ui.compose.views

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.ui.apptheme.AppTheme
import com.grup.ui.compose.AcceptRejectRow
import com.grup.ui.compose.BackPressScaffold
import com.grup.ui.compose.Caption
import com.grup.ui.compose.H1ConfirmTextButton
import com.grup.ui.compose.H1Text
import com.grup.ui.compose.ModalBottomSheetLayout
import com.grup.ui.compose.MoneyAmount
import com.grup.ui.compose.SmallIcon
import com.grup.ui.compose.UserRowCard
import com.grup.ui.compose.asMoneyAmount
import com.grup.ui.compose.collectAsStateWithLifecycle
import com.grup.ui.compose.isoDate
import com.grup.ui.compose.isoTime
import com.grup.ui.viewmodel.SettleActionDetailsViewModel
import kotlinx.coroutines.launch

internal class SettleActionDetailsView(private val actionId: String) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val settleActionDetailsViewModel =
            rememberScreenModel { SettleActionDetailsViewModel(actionId) }

        DebtActionDetailsLayout(
            settleActionDetailsViewModel = settleActionDetailsViewModel,
            navigator = navigator
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DebtActionDetailsLayout(
    settleActionDetailsViewModel: SettleActionDetailsViewModel,
    navigator: Navigator
) {
    val scope = rememberCoroutineScope()
    val acceptSettleTransactionBottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    val settleAction: SettleAction by
        settleActionDetailsViewModel.settleAction.collectAsStateWithLifecycle()

    val myUserInfo: UserInfo? by
        settleActionDetailsViewModel.myUserInfo.collectAsStateWithLifecycle()

    val isMySettleAction: Boolean = settleAction.userInfo.id == myUserInfo?.id

    val (pendingTransactionRecords, completedTransactionRecords) =
        settleAction.transactionRecords.partition { it.status is TransactionRecord.Status.Pending }

    var selectedTransactionType: String by remember {
        mutableStateOf(if (pendingTransactionRecords.isNotEmpty()) "Pending" else "Completed")
    }

    if (selectedTransactionType == "Pending" && pendingTransactionRecords.isEmpty()) {
        selectedTransactionType = "Completed"
    }

    var selectedTransactionRecord: TransactionRecord? by remember { mutableStateOf(null) }

    BackPressScaffold(
        onBackPress = { navigator.pop() },
        actions = {
            if (isMySettleAction && !settleAction.isCompleted) {
                IconButton(
                    onClick = {
                        if (settleAction.pendingAmount == 0.0) {
                            navigator.pop()
                            settleActionDetailsViewModel.cancelSettleAction(
                                onSuccess = { },
                                onError = { }
                            )
                        }
                    }
                ) {
                    SmallIcon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                    )
                }
            }
        }
    ) { padding ->
        ModalBottomSheetLayout(
            sheetState = acceptSettleTransactionBottomSheetState,
            sheetContent = {
                selectedTransactionRecord?.let { transactionRecord ->
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimensions.spacingLarge),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(IntrinsicSize.Min)
                            .padding(AppTheme.dimensions.appPadding)
                    ) {
                        TransactionRecordRowCard(transactionRecord = transactionRecord)
                        AcceptRejectRow(
                            acceptOnClick = {
                                settleActionDetailsViewModel.acceptSettleActionTransactionRecord(
                                    transactionRecord = transactionRecord,
                                    onSuccess = {
                                        scope.launch {
                                            acceptSettleTransactionBottomSheetState.hide()
                                        }
                                    },
                                    onError = { }
                                )
                            },
                            rejectOnClick = {
                                settleActionDetailsViewModel.rejectSettleActionTransactionRecord(
                                    transactionRecord = transactionRecord,
                                    onSuccess = {
                                        scope.launch {
                                            acceptSettleTransactionBottomSheetState.hide()
                                        }
                                    },
                                    onError = { }
                                )
                            }
                        )
                    }
                }
            }
        ) {
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
                                    H1Text(
                                        text = userInfo.user.displayName,
                                        fontSize = AppTheme.typography.extraLargeFont,
                                        maxLines = 2
                                    )
                                    Caption(text = "@${userInfo.user.venmoUsername}")
                                },
                                sideContent = {
                                    Caption(
                                        text = "Settle Request",
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
                                },
                                iconSize = AppTheme.dimensions.largeIconSize
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
                            fontSize = AppTheme.typography.bigMoneyAmountFont
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
                            compareBy<TransactionRecord> {
                                it.userInfo.user.id == settleActionDetailsViewModel.userObject.id
                            }.thenBy {
                                it.status !is TransactionRecord.Status.Rejected
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
                                } else if (isMySettleAction) {
                                    Caption(
                                        text = "Created on ${isoDate(transactionRecord.dateCreated)}"
                                    )
                                }
                            },
                            moneyAmountTextColor = when (transactionRecord.status) {
                                is TransactionRecord.Status.Accepted ->
                                    AppTheme.colors.confirm

                                is TransactionRecord.Status.Rejected -> AppTheme.colors.deny
                                else -> AppTheme.colors.onSecondary
                            },
                            modifier = Modifier.run {
                                if (
                                    isMySettleAction &&
                                    transactionRecord.status is TransactionRecord.Status.Pending
                                )
                                    this.clickable {
                                        selectedTransactionRecord = transactionRecord
                                        scope.launch {
                                            acceptSettleTransactionBottomSheetState.show()
                                        }
                                }
                                else this
                            }
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
                            !settleAction.isCompleted
                        ) {
                            H1Text(
                                text = "Total Completed: " +
                                        completedTransactionRecords.filter {
                                            it.status is TransactionRecord.Status.Accepted
                                        }.sumOf { it.balanceChange }.asMoneyAmount()
                            )
                        } else if (settleAction.transactionRecords.isEmpty()) {
                            Caption(text = "No transactions yet")
                        }
                    }
                }
                with(settleAction) {
                    if (!isMySettleAction && (myUserInfo?.userBalance ?: 0.0) < 0) {
                        H1ConfirmTextButton(
                            text = "Settle",
                            onClick = { navigator.push(SettleActionTransactionView(id)) },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                                .padding(bottom = AppTheme.dimensions.appPadding)
                        )
                    }
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
            Caption(text = "@${transactionRecord.userInfo.user.venmoUsername}")
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
