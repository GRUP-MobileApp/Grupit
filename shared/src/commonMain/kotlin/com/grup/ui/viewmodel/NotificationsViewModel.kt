package com.grup.ui.viewmodel

import cafe.adriel.voyager.core.model.coroutineScope
import com.grup.models.*
import com.grup.ui.models.Notification
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class NotificationsViewModel : LoggedInViewModel() {
    private val _myUserInfosFlow = apiServer.getMyUserInfosAsFlow()
    private val latestDatesFlow: Flow<Map<String, String>> =
        _myUserInfosFlow.map { myUserInfos ->
            myUserInfos.associate { userInfo ->
                userInfo.groupId to userInfo.latestViewDate
            }
        }

    // Hot flow containing all DebtActions across all groups that the user is a part of
    private val _debtActionsFlow = apiServer.getAllDebtActionsAsFlow()
    private val incomingDebtActionsAsNotification: Flow<List<Notification>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.mapNotNull { debtAction ->
                debtAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.userInfo.user.id == userObject.id &&
                            transactionRecord.dateAccepted == TransactionRecord.PENDING
                }?.let { transactionRecord ->
                    Notification.IncomingDebtAction(debtAction, transactionRecord)
                }
            }
        }
    private val outgoingDebtActionsAsNotification: Flow<List<Notification>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.filter { debtAction ->
                debtAction.userInfo.user.id == userObject.id
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
    private val incomingSettleActionTransactionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.mapNotNull { settleAction ->
                settleAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.userInfo.user.id == userObject.id
                            && !transactionRecord.isAccepted
                }?.let { transactionRecord ->
                    Notification.IncomingSettleAction(settleAction, transactionRecord)
                }
            }
        }
    private val outgoingTransactionsOnSettleActionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.filter { settleAction ->
                settleAction.userInfo.user.id == userObject.id
            }.flatMap { settleAction ->
                settleAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.dateAccepted == TransactionRecord.PENDING
                }.map { transactionRecord ->
                    Notification.DebteeAcceptSettleAction(
                        settleAction,
                        transactionRecord
                    )
                }
            }
        }

    private val _groupInvitesFlow = apiServer.getAllGroupInvitesAsFlow()
    private val incomingGroupInvites: StateFlow<List<Notification>> =
        _groupInvitesFlow.map { groupInvites ->
            groupInvites.filter { groupInvite ->
                groupInvite.inviteeId == apiServer.user.id &&
                        groupInvite.dateAccepted == GroupInvite.PENDING
            }.map { groupInvite ->
                Notification.IncomingGroupInvite(groupInvite)
            }
        }.asNotification(emptyList())

    val notifications: StateFlow<Map<String, List<Notification>>> =
        combine(
            incomingDebtActionsAsNotification,
            outgoingDebtActionsAsNotification,
            incomingSettleActionTransactionsAsNotification,
            outgoingTransactionsOnSettleActionsAsNotification,
            incomingGroupInvites
        ) { allNotifications: Array<List<Notification>> ->
            allNotifications.flatMap { it }.sortedByDescending { notification ->
                notification.date
            }.groupBy { notification ->
                notification.groupId
            }
        // TODO: Can change this on onEach to update notificationsCount
        }.combine(latestDatesFlow) { notifications, lastViewDates ->
            notificationsCount.value = notifications.map { (groupId, groupNotifications) ->
                groupId to groupNotifications.count { notification ->
                    !notification.dismissible ||
                    lastViewDates[groupId]?.let { lastViewDate ->
                        notification.date > lastViewDate
                    } ?: true
                }
            }.toMap()
            notifications
        }.asNotification(emptyMap())

    private var notificationsCount: MutableStateFlow<Map<String, Int>> = MutableStateFlow(emptyMap())

    fun logGroupNotificationsDate() = coroutineScope.launch {
        apiServer.updateLatestTime(selectedGroup)
    }

    // DebtAction
    fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        coroutineScope.launch {
            apiServer.acceptDebtAction(debtAction, myTransactionRecord)
        }
    fun rejectDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        coroutineScope.launch {
            apiServer.rejectDebtAction(debtAction, myTransactionRecord)
        }

    // SettleAction
    fun acceptSettleAction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = coroutineScope.launch {
        apiServer.acceptSettleAction(settleAction, transactionRecord)
    }
    fun rejectSettleAction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = coroutineScope.launch {
        apiServer.rejectSettleAction(settleAction, transactionRecord)
    }

    // Group Invite
    fun acceptGroupInvite(groupInvite: GroupInvite) = coroutineScope.launch {
        apiServer.acceptGroupInvite(groupInvite)
    }
    fun rejectGroupInvite(groupInvite: GroupInvite) = coroutineScope.launch {
        apiServer.rejectGroupInvite(groupInvite)
    }

    private fun <T: Iterable<Notification>> T.afterDate(date: String) =
        this.filter { it.date > date }
}
