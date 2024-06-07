package com.grup.service

import com.grup.dbmanager.DatabaseManager
import com.grup.exceptions.AccountDeletionError
import com.grup.exceptions.InvalidUserBalanceException
import com.grup.exceptions.MaximumObjectsException
import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.ISettleActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupService(private val dbManager: DatabaseManager) : KoinComponent {
    private val groupRepository: IGroupRepository by inject()
    private val userInfoRepository: IUserInfoRepository by inject()
    private val debtActionRepository: IDebtActionRepository by inject()
    private val settleActionRepository: ISettleActionRepository by inject()

    private val validationService: ValidationService = ValidationService()

    suspend fun createGroup(user: User, groupName: String): Group {
        val myUserInfos: List<UserInfo> = userInfoRepository.findMyUserInfosAsFlow().first()
        if (myUserInfos.size >= 5) {
            throw MaximumObjectsException("Cannot exceed 5 groups")
        }

        validationService.validateGroupName(groupName)

        return dbManager.write {
            groupRepository.createGroup(this, user, groupName)?.also { group ->
                userInfoRepository.createUserInfo(this, user, group)
            } ?: throw NotCreatedException("Error creating group $groupName")
        }
    }

    suspend fun leaveGroup(userInfo: UserInfo): Boolean {
        if (userInfo.userBalance != 0.0) {
            throw InvalidUserBalanceException("Must be at 0 balance to leave group")
        }

        val settleActions: List<SettleAction> =
            settleActionRepository.findAllSettleActionsAsFlow().map { settleActions ->
                settleActions.filter { it.userInfo.group.id == userInfo.group.id }
            }.first()
        val mySettleActions: List<SettleAction> = settleActions.filter { settleAction ->
            settleAction.userInfo.id == userInfo.id
        }
        // Can't delete account if there are any incomplete SettleAction, since that would
        // mean user also has outstanding positive balance.
        mySettleActions.find { !it.isCompleted }?.let { settleAction ->
            throw AccountDeletionError(
                "You still have an outstanding settlement of ${settleAction.remainingAmount}"
            )
        }
        val outgoingTransactionsPendingOnSettleAction: List<Pair<SettleAction, TransactionRecord>> =
            settleActions.flatMap { settleAction ->
                // Filter since you can have multiple Settle Transactions on a single SettleAction
                settleAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.userInfo.id == userInfo.id &&
                            transactionRecord.status is TransactionRecord.Status.Pending
                }.map { pendingTransactionRecord ->
                    Pair(settleAction, pendingTransactionRecord)
                }
            }

        val debtActions: List<DebtAction> =
            debtActionRepository.findAllDebtActionsAsFlow().map { debtActions ->
                debtActions.filter { it.userInfo.group.id == userInfo.group.id }
            }.first()
        val myDebtActions: List<DebtAction> = debtActions.filter { debtAction ->
            debtAction.userInfo.id == userInfo.id
        }
        val incomingTransactionsPendingOnDebtAction: List<Pair<DebtAction, TransactionRecord>> =
            debtActions.mapNotNull { debtAction ->
                debtAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.userInfo.id == userInfo.id &&
                            transactionRecord.status is TransactionRecord.Status.Pending
                }?.let { pendingTransactionRecord ->
                    Pair(debtAction, pendingTransactionRecord)
                }
            }

        return dbManager.write {
            // Delete user's SettleAction history
            mySettleActions.forEach { settleAction ->
                settleActionRepository.deleteSettleAction(this, settleAction)
            }
            // Set outgoing Settle Transactions as REJECTED
            outgoingTransactionsPendingOnSettleAction.forEach { (settleAction, transactionRecord) ->
                settleActionRepository.updateSettleAction(this, settleAction) {
                    findObject(transactionRecord)?.apply {
                        status = TransactionRecord.Status.Rejected()
                    } ?: throw AccountDeletionError("Internal error, try again later")
                }
            }

            // Delete user's DebtAction history
            myDebtActions.forEach { debtAction ->
                debtActionRepository.deleteDebtAction(this, debtAction)
            }
            // Set incoming Debt Action Transactions as REJECTED
            incomingTransactionsPendingOnDebtAction.forEach { (debtAction, transactionRecord) ->
                debtActionRepository.updateDebtAction(this, debtAction) {
                    findObject(transactionRecord)?.apply {
                        status = TransactionRecord.Status.Rejected()
                    } ?: throw AccountDeletionError("Internal error, try again later")
                }
            }

            userInfoRepository.updateUserInfo(this, userInfo) {
                isActive = false
            }?.isActive == false
        }
    }
}