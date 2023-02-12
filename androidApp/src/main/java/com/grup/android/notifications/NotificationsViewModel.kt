package com.grup.android.notifications

import com.grup.APIServer
import com.grup.android.ViewModel
import com.grup.models.DebtAction
import com.grup.models.GroupInvite
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import kotlinx.coroutines.flow.*

class NotificationsViewModel : ViewModel() {
    // TODO: Remove notifications after joinDate
    // Hot flow containing all subscribed GroupInvites
    private val _groupInvitesFlow = APIServer.getAllGroupInvitesAsFlow()
    private val incomingGroupInvitesAsNotification: Flow<List<Notification>> =
        _groupInvitesFlow.map { groupInvites ->
            groupInvites.filter { groupInvite ->
                groupInvite.invitee!! == userObject.getId() &&
                        groupInvite.dateAccepted == GroupInvite.PENDING
            }.map { groupInvite ->
                Notification.IncomingGroupInvite(groupInvite)
            }
        }
    private val outgoingGroupInvitesAsNotification: Flow<List<Notification>> =
        _groupInvitesFlow.map { groupInvites ->
            groupInvites.filter { groupInvite ->
                groupInvite.inviter!! == userObject.getId() &&
                        groupInvite.dateAccepted != GroupInvite.PENDING
            }.map { groupInvite ->
                Notification.InviteeAcceptOutgoingGroupInvite(groupInvite)
            }
        }

    // Hot flow containing all DebtActions across all groups that the user is a part of
    private val _debtActionsFlow = APIServer.getAllDebtActionsAsFlow()
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
                    transactionRecord.dateAccepted != TransactionRecord.PENDING
                }.map { transactionRecord ->
                    Notification.DebtorAcceptOutgoingDebtAction(debtAction, transactionRecord)
                }
            }
        }

    // Hot flow containing all Settle across all groups that the user is a part of
    private val _settleActionsFlow = APIServer.getAllSettleActionsAsFlow()
    private val newSettleActionsAsNotification: Flow<List<Notification>> =
        _settleActionsFlow.map { settleActions ->
            settleActions.mapNotNull { settleAction ->
                if (settleAction.debteeUserInfo!!.userId!! != userObject.getId()) {
                    Notification.NewSettleAction(settleAction)
                } else {
                    null
                }
            }
        }
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
                            && transactionRecord.dateAccepted != TransactionRecord.PENDING
                }?.let { transactionRecord ->
                    Notification.DebteeAcceptSettleActionTransaction(settleAction, transactionRecord)
                }
            }
        }

    val notifications: StateFlow<List<Notification>> =
        combine(
            incomingGroupInvitesAsNotification,
            outgoingGroupInvitesAsNotification,
            incomingDebtActionsAsNotification,
            outgoingDebtActionsAsNotification,
            newSettleActionsAsNotification,
            incomingTransactionsOnSettleActionsAsNotification,
            outgoingTransactionsOnSettleActionsAsNotification
        ) { allNotifications: Array<List<Notification>> ->
            allNotifications.flatMap { it }.sortedByDescending { notification ->
                notification.date
            }
        }.asNotification()


    // Group Invite
    fun acceptInviteToGroup(groupInvite: GroupInvite) = APIServer.acceptInviteToGroup(groupInvite)

    // DebtAction
    fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        APIServer.acceptDebtAction(debtAction, myTransactionRecord)

    // SettleAction
    fun acceptSettleActionTransaction(settleAction: SettleAction,
                                      transactionRecord: TransactionRecord) =
        APIServer.acceptSettleActionTransaction(settleAction, transactionRecord)

    private fun <T: Iterable<Notification>> T.afterJoinDate(date: String) =
        this.filter { it.date > date }
}