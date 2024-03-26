package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.screenModelScope
import com.grup.models.DebtAction
import com.grup.models.GroupInvite
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.ui.models.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

internal class NotificationsViewModel : LoggedInViewModel() {
    // Hot flow containing all DebtActions across all groups that the user is a part of
    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
    private val incomingDebtActionsAsNotification: Flow<List<Notification>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.mapNotNull { debtAction ->
                debtAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.userInfo.user.id == userObject.id &&
                            transactionRecord.status is TransactionRecord.Status.Pending
                }?.let { transactionRecord ->
                    Notification.IncomingDebtAction(debtAction, transactionRecord)
                }
            }
        }
    private val debtorAcceptOutgoingDebtActionsAsNotification: Flow<List<Notification>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.userInfo.user.id == userObject.id
            }.flatMap { debtAction ->
                debtAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.status is TransactionRecord.Status.Accepted
                }.map { transactionRecord ->
                    Notification.DebtorAcceptOutgoingDebtAction(debtAction, transactionRecord)
                }
            }
        }

    // Hot flow containing all Settle across all groups that the user is a part of
    private val _settleActionsFlow = apiServer.getAllSettleActionsAsFlow()
    private val newSettleActionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                !settleAction.isCompleted
            }.map { settleAction ->
                Notification.NewSettleAction(settleAction)
            }
        }
    private val incomingSettleActionTransactionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.mapNotNull { settleAction ->
                settleAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.userInfo.user.id == userObject.id
                            && transactionRecord.status !is TransactionRecord.Status.Accepted
                }?.let { transactionRecord ->
                    Notification.IncomingSettleActionTransaction(settleAction, transactionRecord)
                }
            }
        }
    private val outgoingTransactionsOnSettleActionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.userInfo.user.id == userObject.id
            }.flatMap { settleAction ->
                settleAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.status is TransactionRecord.Status.Accepted
                }.map { transactionRecord ->
                    Notification.DebteeAcceptOutgoingSettleActionTransaction(
                        settleAction,
                        transactionRecord
                    )
                }
            }
        }

    private val _groupInvitesFlow = apiServer.getAllGroupInvitesAsFlow()
    private val incomingGroupInvites: Flow<List<Notification>> =
        _groupInvitesFlow.map { groupInvites ->
            groupInvites.filter { groupInvite ->
                groupInvite.inviteeId == userObject.id
            }.map { groupInvite ->
                Notification.IncomingGroupInvite(groupInvite)
            }
        }

    val notifications: StateFlow<List<Notification>> =
        combine(
            incomingDebtActionsAsNotification,
            debtorAcceptOutgoingDebtActionsAsNotification,
            newSettleActionsAsNotification,
            incomingSettleActionTransactionsAsNotification,
            outgoingTransactionsOnSettleActionsAsNotification,
            incomingGroupInvites
        ) { allNotifications: Array<List<Notification>> ->
            allNotifications.flatMap { it }.sortedByDescending { notification ->
                notification.date
            }
        // TODO: Add unread notificationsCount
        }.asNotification(emptyList())

    private var notificationsCount: MutableStateFlow<Int> = MutableStateFlow(0)

    fun logGroupNotificationsDate() = screenModelScope.launch {
        apiServer.updateLatestTime()
    }

    // DebtAction
    fun acceptDebtAction(debtAction: DebtAction, transactionRecord: TransactionRecord) =
        screenModelScope.launch {
            apiServer.acceptDebtAction(debtAction, transactionRecord)
        }
    fun rejectDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        screenModelScope.launch {
            apiServer.rejectDebtAction(debtAction, myTransactionRecord)
        }

    // SettleAction
    fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = screenModelScope.launch {
        apiServer.acceptSettleActionTransaction(settleAction, transactionRecord)
    }
    fun rejectSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = screenModelScope.launch {
        apiServer.rejectSettleActionTransaction(settleAction, transactionRecord)
    }

    // Group Invite
    fun acceptGroupInvite(groupInvite: GroupInvite) = screenModelScope.launch {
        apiServer.acceptGroupInvite(groupInvite)
    }
    fun rejectGroupInvite(groupInvite: GroupInvite) = screenModelScope.launch {
        apiServer.rejectGroupInvite(groupInvite)
    }
}
