package com.grup.ui.viewmodel

import com.grup.exceptions.APIException
import com.grup.models.GroupInvite
import com.grup.models.TransactionRecord
import com.grup.ui.models.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

internal class NotificationsViewModel : LoggedInViewModel() {
    // Hot flow containing all DebtActions across all groups that the user is a part of
    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
    private val incomingDebtActionsAsNotification: Flow<List<Notification>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.userInfo.user.id != userId
            }.mapNotNull { debtAction ->
                debtAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.userInfo.user.id == userId &&
                            transactionRecord.status is TransactionRecord.Status.Pending
                }?.let { transactionRecord ->
                    Notification.IncomingDebtAction(debtAction, transactionRecord)
                }
            }
        }
    private val debtorAcceptOutgoingDebtActionsAsNotification: Flow<List<Notification>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.flatMap { debtAction ->
                debtAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.status !is TransactionRecord.Status.Pending &&
                            transactionRecord.userInfo.user.id != userId
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
                settleAction.userInfo.user.id != userId && !settleAction.isCompleted
            }.map { settleAction ->
                Notification.NewSettleAction(settleAction)
            }
        }
    private val incomingSettleActionTransactionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.userInfo.user.id == userId
            }.flatMap { settleAction ->
                settleAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.status is TransactionRecord.Status.Pending
                }.map { transactionRecord ->
                    Notification.IncomingSettleActionTransaction(settleAction, transactionRecord)
                }
            }
        }
    private val outgoingTransactionsOnSettleActionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.mapNotNull { settleAction ->
                settleAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.userInfo.user.id == userId
                            && transactionRecord.status !is TransactionRecord.Status.Pending
                }?.let { transactionRecord ->
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
                groupInvite.inviteeId == userId
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

    fun logGroupNotificationsDate() = launchJob {
        apiServer.updateLatestTime()
    }

    // Group Invite
    fun acceptGroupInvite(
        groupInvite: GroupInvite,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.acceptGroupInvite(groupInvite)
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
    fun rejectGroupInvite(
        groupInvite: GroupInvite,
        onSuccess: () -> Unit,
        onError: (String?) -> Unit
    ) = launchJob {
        try {
            apiServer.rejectGroupInvite(groupInvite)
            onSuccess()
        } catch (e: APIException) {
            onError(e.message)
        }
    }
}
