package com.grup

import com.grup.dbmanager.realm.DebugRealmManager
import com.grup.dbmanager.realm.ReleaseRealmManager
import com.grup.exceptions.login.LoginException
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.dbmanager.DatabaseManager
import com.grup.models.DebtAction
import com.grup.models.GroupInvite
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.other.AccountSettings
import com.grup.platform.signin.AuthManager
import com.grup.service.AccountSettingsService
import com.grup.service.DebtActionService
import com.grup.service.GroupInviteService
import com.grup.service.GroupService
import com.grup.service.SettleActionService
import com.grup.service.UserInfoService
import com.grup.service.UserService
import kotlin.coroutines.cancellation.CancellationException

class APIServer private constructor(
    private val dbManager: DatabaseManager
) {
    private val userService: UserService = UserService(dbManager)
    private val groupService: GroupService = GroupService(dbManager)
    private val userInfoService: UserInfoService = UserInfoService(dbManager)
    private val groupInviteService: GroupInviteService = GroupInviteService(dbManager)
    private val debtActionService: DebtActionService = DebtActionService(dbManager)
    private val settleActionService: SettleActionService = SettleActionService(dbManager)

    private val accountSettingsService: AccountSettingsService = AccountSettingsService()

    // User
    val user: User
        get() = userService.getMyUser()
            ?: throw UserObjectNotFoundException()
    val authProvider: AuthManager.AuthProvider
        get() = dbManager.authProvider
    suspend fun registerUser(
        username: String,
        displayName: String,
        venmoUsername: String?,
        profilePicture: ByteArray
    ) = userService.createMyUser(username, displayName, venmoUsername, profilePicture)
    suspend fun usernameExists(username: String) = userService.getUserByUsername(username) != null
    suspend fun updateUser(block: User.() -> Unit) = userService.updateUser(user, block)
    suspend fun updateProfilePicture(profilePicture: ByteArray) =
        userService.updateProfilePicture(user, profilePicture)
    suspend fun updateLatestTime() = userService.updateLatestTime(user)

    // Group
    suspend fun createGroup(groupName: String) = groupService.createGroup(user, groupName)

    // UserInfo
    fun getMyUserInfosAsFlow() = userInfoService.getMyUserInfosAsFlow()
    fun getAllUserInfosAsFlow() = userInfoService.getAllUserInfosAsFlow()

    // GroupInvite
    suspend fun createGroupInvite(inviterUserInfo: UserInfo, inviteeUsername: String) =
        groupInviteService.createGroupInvite(inviterUserInfo, inviteeUsername)
    suspend fun acceptGroupInvite(groupInvite: GroupInvite) =
        groupInviteService.acceptGroupInvite(groupInvite, user)
    suspend fun rejectGroupInvite(groupInvite: GroupInvite) =
        groupInviteService.rejectGroupInvite(groupInvite)
    fun getAllGroupInvitesAsFlow() =
        groupInviteService.getAllGroupInvitesAsFlow()

    // DebtAction
    suspend fun createDebtAction(
        debtee: UserInfo,
        transactionRecords: List<TransactionRecord>,
        message: String,
    ) = debtActionService.createDebtAction(debtee, transactionRecords, message)
    suspend fun acceptDebtAction(debtAction: DebtAction, transactionRecord: TransactionRecord) =
        debtActionService.acceptDebtAction(debtAction, transactionRecord)
    suspend fun rejectDebtAction(debtAction: DebtAction, transactionRecord: TransactionRecord) =
        debtActionService.rejectDebtAction(debtAction, transactionRecord)
    fun getAllDebtActionsAsFlow() = debtActionService.getAllDebtActionsAsFlow()

    // SettleAction
    suspend fun createSettleAction(debtee: UserInfo, settleActionAmount: Double) =
        settleActionService.createSettleAction(debtee, settleActionAmount)
    suspend fun createSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = settleActionService.createSettleActionTransaction(settleAction, transactionRecord)
    suspend fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = settleActionService.acceptSettleActionTransaction(settleAction, transactionRecord)
    suspend fun rejectSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = settleActionService.rejectSettleActionTransaction(settleAction, transactionRecord)
    fun getAllSettleActionsAsFlow() = settleActionService.getAllSettleActionsAsFlow()

    // Account Settings
    fun getGroupNotificationType(notificationType: AccountSettings.GroupNotificationType) =
        accountSettingsService.getGroupNotificationType(notificationType)

    fun toggleGroupNotificationType(notificationType: AccountSettings.GroupNotificationType) =
        accountSettingsService.toggleGroupNotificationType(notificationType)


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

    suspend fun logOut() = dbManager.logOut()
}
