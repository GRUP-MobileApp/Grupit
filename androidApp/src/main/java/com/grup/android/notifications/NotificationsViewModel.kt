package com.grup.android.notifications

import androidx.lifecycle.viewModelScope
import com.grup.android.LoggedInViewModel
import com.grup.android.MainViewModel
import com.grup.models.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class NotificationsViewModel : LoggedInViewModel() {
    companion object {
        var notificationsAmount: MutableStateFlow<Map<String, Int>> =
            MutableStateFlow(emptyMap())
    }
    // TODO: Remove notifications after joinDate

    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    private val latestDatesFlow: Flow<Map<String, String>> =
        _myUserInfosFlow.map { myUserInfos ->
            myUserInfos.associate { userInfo ->
                userInfo.groupId!! to userInfo.latestViewDate
            }
        }

    // Hot flow containing all DebtActions across all groups that the user is a part of
    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
    private val incomingDebtActionsAsNotification: Flow<List<Notification>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.mapNotNull { debtAction ->
                debtAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.debtorUserInfo!!.userId!! == userObject.getId() &&
                            transactionRecord.dateAccepted == TransactionRecord.PENDING
                }?.let { transactionRecord ->
                    Notification.IncomingDebtAction(debtAction, transactionRecord)
                }
            }
        }
    private val outgoingDebtActionsAsNotification: Flow<List<Notification>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.debteeUserInfo!!.userId!! == userObject.getId()
            }.flatMap { debtAction ->
                debtAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.isAccepted
                }.map { transactionRecord ->
                    Notification.DebtorAcceptOutgoingDebtAction(debtAction, transactionRecord)
                }
            }
        }

    // Hot flow containing all Settle across all groups that the user is a part of
    private val _settleActionsFlow = apiServer.getAllSettleActionsAsFlow()
    private val incomingTransactionsOnSettleActionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.debteeUserInfo!!.userId!! == userObject.getId()
            }.flatMap { debtAction ->
                debtAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.dateAccepted == TransactionRecord.PENDING
                }.map { transactionRecord ->
                    Notification.IncomingTransactionOnSettleAction(debtAction, transactionRecord)
                }
            }
        }
    private val outgoingTransactionsOnSettleActionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.mapNotNull { settleAction ->
                settleAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.debtorUserInfo!!.userId!! == userObject.getId()
                            && transactionRecord.isAccepted
                }?.let { transactionRecord ->
                    Notification.DebteeAcceptSettleActionTransaction(settleAction, transactionRecord)
                }
            }
        }

    val notifications: StateFlow<Map<String, List<Notification>>> =
        combine(
            incomingDebtActionsAsNotification,
            outgoingDebtActionsAsNotification,
            incomingTransactionsOnSettleActionsAsNotification,
            outgoingTransactionsOnSettleActionsAsNotification
        ) { allNotifications: Array<List<Notification>> ->
            allNotifications.flatMap { it }.sortedByDescending { notification ->
                notification.date
            }.groupBy { notification ->
                notification.groupId
            }
        }.combine(latestDatesFlow) { notifications, lastViewDates ->
            notificationsAmount.value = notifications.map { groupEntry ->
                groupEntry.key to groupEntry.value.count { notification ->
                    !notification.dismissible ||
                    lastViewDates[groupEntry.key]?.let { lastViewDate ->
                        notification.date > lastViewDate
                    } ?: true
                }
            }.toMap()
            notifications
        }.asNotification(emptyMap())

    fun logGroupNotificationsDate() = viewModelScope.launch {
        apiServer.updateLatestTime(MainViewModel.selectedGroup)
    }

    // DebtAction
    fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        viewModelScope.launch {
            apiServer.acceptDebtAction(debtAction, myTransactionRecord)
        }
    fun rejectDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        viewModelScope.launch {
            apiServer.rejectDebtAction(debtAction, myTransactionRecord)
        }

    // SettleAction
    fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = viewModelScope.launch {
        apiServer.acceptSettleActionTransaction(settleAction, transactionRecord)
    }
    fun rejectSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = viewModelScope.launch {
        apiServer.rejectSettleActionTransaction(settleAction, transactionRecord)
    }

    private fun <T: Iterable<Notification>> T.afterDate(date: String) =
        this.filter { it.date > date }
}
