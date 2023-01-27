package com.grup.android.notifications

import com.grup.APIServer
import com.grup.android.ViewModel
import com.grup.models.GroupInvite
import kotlinx.coroutines.flow.*

class NotificationsViewModel : ViewModel() {
    private val groupInvitesAsNotification: Flow<List<Notification>> =
        APIServer.getAllGroupInvitesAsFlow().map { groupInvites ->
            groupInvites.map { groupInvite ->
                Notification.GroupInvite(groupInvite)
            }
        }

    // Hot flow containing all DebtActions across all groups that the user is a part of
    private val _debtActionsFlow = APIServer.getAllDebtActionsAsFlow()
    private val incomingDebtActionsAsNotification: Flow<List<Notification>> =
        _debtActionsFlow.map { debtActions ->
            debtActions.mapNotNull { debtAction ->
                debtAction.debtTransactions.find { transactionRecord ->
                    transactionRecord.debtor!! == userObject.getId()
                }?.let { transactionRecord ->
                    Notification.IncomingDebtAction(debtAction, transactionRecord)
                }
            }
        }

    val notifications: StateFlow<List<Notification>> =
        merge(groupInvitesAsNotification, incomingDebtActionsAsNotification).map { notifications ->
            notifications.sortedBy { notification ->
                notification.date
            }
        }.asNotifications()


    fun acceptInviteToGroup(groupInvite: GroupInvite) = APIServer.acceptInviteToGroup(groupInvite)
}