package com.grup.service

import com.grup.dbmanager.DatabaseManager
import com.grup.exceptions.AccountDeletionError
import com.grup.exceptions.EmptyArgumentException
import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IGroupInviteRepository
import com.grup.interfaces.IImagesRepository
import com.grup.interfaces.ISettleActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.interfaces.IUserRepository
import com.grup.models.DebtAction
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.other.getCurrentTime
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserService(private val dbManager: DatabaseManager) : KoinComponent {
    private val userRepository: IUserRepository by inject()
    private val userInfoRepository: IUserInfoRepository by inject()
    private val debtActionRepository: IDebtActionRepository by inject()
    private val settleActionRepository: ISettleActionRepository by inject()
    private val groupInviteRepository: IGroupInviteRepository by inject()

    private val imagesRepository: IImagesRepository by inject()
    private val validationService: ValidationService = ValidationService()

    suspend fun createMyUser(
        username: String,
        displayName: String,
        venmoUsername: String?,
        profilePicture: ByteArray
    ): User {
        return dbManager.write {
            userRepository.createMyUser(this, username, displayName, venmoUsername)
                ?: throw NotCreatedException("Error creating user object")
        }.also { user ->
            updateProfilePicture(user, profilePicture)
        }
    }

    fun getMyUser(checkDB: Boolean = false): User? {
        return userRepository.findMyUser(checkDB)
    }

    suspend fun getUserByUsername(username: String): User? {
        if (username.isBlank()) {
            throw EmptyArgumentException("Please enter a username")
        }
        return userRepository.findUserByUsername(username)
    }

    suspend fun updateUser(user: User, block: User.() -> Unit): User = dbManager.write {
        userRepository.updateUser(this, user) {
            apply(block)
            if (venmoUsername?.isEmpty() == true) {
                venmoUsername = null
            }
            validationService.validateName(displayName)
            venmoUsername?.let { validationService.validateVenmoUsername(it) }
        }
    }

    suspend fun updateLatestTime(user: User) = updateUser(user) {
        latestViewDate = getCurrentTime()
    }

    suspend fun updateProfilePicture(user: User, profilePicture: ByteArray) {
        user.profilePictureURL?.let {
            imagesRepository.deleteProfilePicture(it)
        }

        imagesRepository.uploadProfilePicture(user.id, profilePicture)?.let { pfpURL ->
            dbManager.write {
                userRepository.updateUser(this, user) {
                    this.profilePictureURL = pfpURL
                }
            }
        }
    }

    suspend fun deleteUser(user: User) {
        val myUserInfos: List<UserInfo> =
            userInfoRepository.findMyUserInfosAsFlow(false).first()
        // Check for all 0 balance
        if (myUserInfos.any { it.userBalance != 0.0 && it.isActive }) {
            throw AccountDeletionError("All groups must be at 0 balance")
        }

        val settleActions: List<SettleAction> =
            settleActionRepository.findAllSettleActionsAsFlow().first()
        val mySettleActions: List<SettleAction> = settleActions.filter { settleAction ->
            settleAction.userInfo.user.id == user.id
        }
        // Can't delete account if there are any incomplete SettleAction, since that would
        // mean user also has outstanding positive balance.
        mySettleActions.find { !it.isCompleted }?.let { settleAction ->
            throw AccountDeletionError(
                "You still have an outstanding settlement in " +
                        settleAction.userInfo.group.groupName
            )
        }
        val outgoingTransactionsPendingOnSettleAction: List<Pair<SettleAction, TransactionRecord>> =
            settleActions.flatMap { settleAction ->
                // Filter since you can have multiple Settle Transactions on a single SettleAction
                settleAction.transactionRecords.filter { transactionRecord ->
                    transactionRecord.userInfo.user.id == user.id &&
                        transactionRecord.status is TransactionRecord.Status.Pending
                }.map { pendingTransactionRecord ->
                    Pair(settleAction, pendingTransactionRecord)
                }
            }

        val debtActions: List<DebtAction> =
            debtActionRepository.findAllDebtActionsAsFlow().first()
        val myDebtActions: List<DebtAction> = debtActions.filter { debtAction ->
            debtAction.userInfo.user.id == user.id
        }
        val incomingTransactionsPendingOnDebtAction: List<Pair<DebtAction, TransactionRecord>> =
            debtActions.mapNotNull { debtAction ->
                debtAction.transactionRecords.find { transactionRecord ->
                    transactionRecord.userInfo.user.id == user.id &&
                            transactionRecord.status is TransactionRecord.Status.Pending
                }?.let { pendingTransactionRecord ->
                    Pair(debtAction, pendingTransactionRecord)
                }
            }

        // Save url before deleting user object
        val profilePictureUrl = user.profilePictureURL

        dbManager.write {
            // Delete all user's incoming and outgoing GroupInvites
            groupInviteRepository.deleteAllGroupInvites(this)

            // Delete user's SettleAction history
            mySettleActions.forEach { settleAction ->
                settleActionRepository.deleteSettleAction(this, settleAction)
            }
            // Delete outgoing Settle Transactions
            outgoingTransactionsPendingOnSettleAction.forEach { (settleAction, transactionRecord) ->
                settleActionRepository.updateSettleAction(this, settleAction) {
                    transactionRecords.remove(
                        findObject(transactionRecord)
                            ?: throw AccountDeletionError("Internal error, try again later")
                    )
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

            // Set all userInfo to not active
            myUserInfos.forEach { userInfo ->
                userInfoRepository.updateUserInfo(this, userInfo) {
                    isActive = false
                    removeUser()
                }
            }

            // Delete User
            userRepository.deleteUser(this, user)
        }
        profilePictureUrl?.let { imagesRepository.deleteProfilePicture(it) }
    }
}