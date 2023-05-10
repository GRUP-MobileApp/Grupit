package com.grup

import com.grup.controllers.*
import com.grup.di.*
import com.grup.exceptions.login.LoginException
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.models.*
import com.grup.other.AWS_IMAGES_BUCKET_NAME
import com.grup.interfaces.DBManager
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlin.coroutines.cancellation.CancellationException

class APIServer private constructor(
    private val dbManager: DBManager
) {
    private val userController: UserController = UserController()
    private val groupController: GroupController = GroupController()
    private val userInfoController: UserInfoController = UserInfoController()
    private val groupInviteController: GroupInviteController = GroupInviteController()
    private val debtActionController: DebtActionController = DebtActionController()
    private val settleActionController: SettleActionController = SettleActionController()

    // User
    val user: User
        get() = userController.getMyUser()
            ?: throw UserObjectNotFoundException()
    suspend fun registerUser(
        username: String,
        displayName: String,
        profilePicture: ByteArray?
    ) = userController.createUser(username, displayName, profilePicture)
    suspend fun validUsername(username: String) = !userController.usernameExists(username)

    // Group
    fun createGroup(groupName: String) = groupController.createGroup(user, groupName)
    fun getAllGroupsAsFlow() = groupController.getAllGroupsAsFlow()

    // UserInfo
    fun getMyUserInfosAsFlow() = userInfoController.getMyUserInfosAsFlow()
    fun getAllUserInfosAsFlow() = userInfoController.getAllUserInfosAsFlow()
    suspend fun updateLatestTime(group: Group) = userInfoController.updateLatestTime(group)

    // GroupInvite
    suspend fun inviteUserToGroup(username: String, group: Group) =
        groupInviteController.createGroupInvite(user, username, group)
    suspend fun acceptGroupInvite(groupInvite: GroupInvite) =
        groupInviteController.acceptGroupInvite(groupInvite, user)
    suspend fun rejectGroupInvite(groupInvite: GroupInvite) =
        groupInviteController.rejectGroupInvite(groupInvite)
    fun getAllGroupInvitesAsFlow() =
        groupInviteController.getAllGroupInvitesAsFlow()

    // DebtAction
    fun createDebtAction(
        transactionRecords: List<TransactionRecord>,
        debtee: UserInfo,
        message: String
    ) = debtActionController.createDebtAction(transactionRecords, debtee, message)
    suspend fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        debtActionController.acceptDebtAction(debtAction, myTransactionRecord)
    suspend fun rejectDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        debtActionController.rejectDebtAction(debtAction, myTransactionRecord)
    fun getAllDebtActionsAsFlow() =
        debtActionController.getAllDebtActionsAsFlow()

    // SettleAction
    suspend fun createSettleAction(settleAmount: Double, debtee: UserInfo) =
        settleActionController.createSettleAction(settleAmount, debtee)
    fun createSettleActionTransaction(
        settleAction: SettleAction,
        myTransactionRecord: TransactionRecord
    ) = settleActionController.createSettleActionTransaction(settleAction, myTransactionRecord)
    suspend fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = settleActionController.acceptSettleActionTransaction(settleAction, transactionRecord)
    suspend fun rejectSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = settleActionController.rejectSettleActionTransaction(settleAction, transactionRecord)
    fun getAllSettleActionsAsFlow() =
        settleActionController.getAllSettleActionsAsFlow()

    companion object Login {
        @Throws(LoginException::class, CancellationException::class)
        suspend fun loginEmailAndPassword(email: String, password: String): APIServer =
            APIServer(DebugRealmManager.loginEmailPassword(email, password))

        @Throws(LoginException::class, CancellationException::class)
        suspend fun registerEmailAndPassword(email: String, password: String): APIServer =
            APIServer(DebugRealmManager.registerEmailPassword(email, password))

        @Throws(LoginException::class, CancellationException::class)
        suspend fun loginGoogleAccountToken(googleAccountToken: String): APIServer =
            APIServer(RealmManager.loginGoogle(googleAccountToken))
    }

    suspend fun logOut() = dbManager.close()
}
