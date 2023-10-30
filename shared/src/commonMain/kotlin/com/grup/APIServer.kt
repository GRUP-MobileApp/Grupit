package com.grup

import com.grup.controllers.*
import com.grup.di.*
import com.grup.exceptions.login.LoginException
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.models.*
import com.grup.interfaces.DBManager
import com.grup.other.AccountSettings
import com.grup.platform.signin.AuthManager
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

    private val accountSettingsController: AccountSettingsController = AccountSettingsController()

    // User
    val user: User
        get() = userController.getMyUser()
            ?: throw UserObjectNotFoundException()
    val authProvider: AuthManager.AuthProvider
        get() = dbManager.authProvider
    suspend fun registerUser(
        username: String,
        displayName: String,
        profilePicture: ByteArray
    ) = userController.createUser(username, displayName, profilePicture)
    suspend fun validUsername(username: String) = !userController.usernameExists(username)

    // Group
    suspend fun createGroup(groupName: String) = groupController.createGroup(user, groupName)
    fun getAllGroupsAsFlow() = groupController.getAllGroupsAsFlow()

    // UserInfo
    fun getMyUserInfosAsFlow() = userInfoController.getMyUserInfosAsFlow()
    fun getAllUserInfosAsFlow() = userInfoController.getAllUserInfosAsFlow()
    suspend fun updateLatestTime(user: User) = userController.updateLatestTime(user)

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
        debtee: UserInfo,
        message: String,
        transactionRecords: List<TransactionRecord>
    ) = debtActionController.createDebtAction(transactionRecords, debtee, message)
    suspend fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        debtActionController.acceptDebtAction(debtAction, myTransactionRecord)
    suspend fun rejectDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        debtActionController.rejectDebtAction(debtAction, myTransactionRecord)
    fun getAllDebtActionsAsFlow() =
        debtActionController.getAllDebtActionsAsFlow()

    // SettleAction
    suspend fun createSettleAction(debtor: UserInfo, transactionRecords: List<TransactionRecord>) =
        settleActionController.createSettleAction(debtor, transactionRecords)
    suspend fun acceptSettleAction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = settleActionController.acceptSettleAction(settleAction, transactionRecord)
    suspend fun rejectSettleAction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = settleActionController.rejectSettleAction(settleAction, transactionRecord)
    fun getAllSettleActionsAsFlow() =
        settleActionController.getAllSettleActionsAsFlow()

    // Account Settings
    fun getGroupNotificationType(notificationType: AccountSettings.GroupNotificationType) =
        accountSettingsController.getGroupNotificationType(notificationType)

    fun toggleGroupNotificationType(notificationType: AccountSettings.GroupNotificationType) =
        accountSettingsController.toggleGroupNotificationType(notificationType)


    companion object Login {
        @Throws(LoginException::class, CancellationException::class)
        suspend fun debugSilentSignIn(): APIServer? =
            DebugRealmManager.silentSignIn()?.let { realmManager ->
                APIServer(realmManager)
            }

        @Throws(LoginException::class, CancellationException::class)
        suspend fun loginEmailAndPassword(email: String, password: String): APIServer =
            APIServer(DebugRealmManager.loginEmailPassword(email, password))

        @Throws(LoginException::class, CancellationException::class)
        suspend fun registerEmailAndPassword(email: String, password: String): APIServer =
            APIServer(DebugRealmManager.registerEmailPassword(email, password))

        @Throws(LoginException::class, CancellationException::class)
        suspend fun releaseSilentSignIn(): APIServer? =
            ReleaseRealmManager.silentSignIn()?.let { realmManager ->
                APIServer(realmManager)
            }

        @Throws(LoginException::class, CancellationException::class)
        suspend fun loginGoogleAccountToken(googleAccountToken: String): APIServer =
            APIServer(ReleaseRealmManager.loginGoogle(googleAccountToken))
    }

    suspend fun logOut() = dbManager.close()
}
