package com.grup

import com.grup.controllers.*
import com.grup.di.*
import com.grup.di.openSyncedRealm
import com.grup.exceptions.EntityAlreadyExistsException
import com.grup.exceptions.login.InvalidEmailPasswordException
import com.grup.exceptions.login.NotLoggedInException
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.models.*
import com.grup.models.User
import com.grup.other.RealmUser
import com.grup.other.APP_ID
import com.grup.other.AWS_IMAGES_BUCKET_NAME
import com.grup.service.Notifications
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.*
import io.realm.kotlin.mongodb.exceptions.BadRequestException
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import kotlinx.coroutines.Job
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

class APIServer private constructor(
    internal val realm: Realm
) {
    private val realmUser: RealmUser
        get() = app.currentUser ?: throw NotLoggedInException()

    private val subscriptionsJob: Job = startSubscriptionSyncJob()

    companion object Login {
        internal val app: App = App.create(APP_ID)

        private suspend fun initializeAPIServer(credentials: Credentials): APIServer {
            app.login(credentials)
            val realm = openSyncedRealm(app.currentUser!!)
            loadKoinModules(
                module {
                    single { realm }
                }
            )
            loadKoinModules(releaseAppModules)
            return APIServer(realm)
        }

        suspend fun loginEmailAndPassword(email: String, password: String): APIServer {
            try {
                return initializeAPIServer(Credentials.emailPassword(email, password))
            } catch (e: InvalidCredentialsException) {
                throw InvalidEmailPasswordException()
            } catch (e: IllegalArgumentException) {
                throw InvalidEmailPasswordException(e.message)
            }
        }

        suspend fun registerEmailAndPassword(email: String, password: String): APIServer {
            try {
                app.emailPasswordAuth.registerUser(email, password)
            } catch (e: UserAlreadyExistsException) {
                throw EntityAlreadyExistsException("Email already exists")
            } catch (e: IllegalArgumentException) {
                throw InvalidEmailPasswordException(e.message)
            } catch (e: BadRequestException) {
                // TODO: Bad email/bad password exception
            }
            return loginEmailAndPassword(email, password)
        }

        suspend fun loginGoogleAccountToken(googleAccountToken: String): APIServer {
            try {
                return initializeAPIServer(
                    Credentials.google(googleAccountToken, GoogleAuthType.ID_TOKEN)
                )
            } catch (e: Exception) {
                throw Exception()
            }
        }
    }

    private val userController = UserController()
    private val groupController = GroupController()
    private val userInfoController = UserInfoController()
    private val groupInviteController = GroupInviteController()
    private val debtActionController = DebtActionController()
    private val settleActionController = SettleActionController()

    // User
    val user: User
        get() = userController.getMyUser()
            ?: throw UserObjectNotFoundException()
    suspend fun registerUser(
        username: String,
        displayName: String,
        profilePicture: ByteArray
    ) = userController.createUser(username, displayName, profilePicture)
    suspend fun validUsername(username: String) = !userController.usernameExists(username)

    // Group
    fun createGroup(groupName: String) = groupController.createGroup(user, groupName)
    fun getAllGroupsAsFlow() = groupController.getAllGroupsAsFlow()

    // UserInfo
    fun getMyUserInfosAsFlow() = userInfoController.getMyUserInfosAsFlow(user)
    fun getAllUserInfosAsFlow() = userInfoController.getAllUserInfosAsFlow()

    // GroupInvite
    suspend fun inviteUserToGroup(username: String, group: Group) =
        groupInviteController.createGroupInvite(user, username, group)
    fun acceptInviteToGroup(groupInvite: GroupInvite) =
        groupInviteController.acceptInviteToGroup(groupInvite, user)
    fun getAllGroupInvitesAsFlow() = groupInviteController.getAllGroupInvitesAsFlow()

    // DebtAction
    fun createDebtAction(
        transactionRecords: List<TransactionRecord>,
        debtee: UserInfo,
        message: String
    ) = debtActionController.createDebtAction(transactionRecords, debtee, message)
    fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        debtActionController.acceptDebtAction(debtAction, myTransactionRecord)
    fun getAllDebtActionsAsFlow() = debtActionController.getAllDebtActionsAsFlow()

    // SettleAction
    fun createSettleAction(settleAmount: Double, debtee: UserInfo) =
        settleActionController.createSettleAction(settleAmount, debtee)
    fun createSettleActionTransaction(
        settleAction: SettleAction,
        myTransactionRecord: TransactionRecord
    ) = settleActionController.createSettleActionTransaction(settleAction, myTransactionRecord)
    fun acceptSettleActionTransaction(settleAction: SettleAction,
                                      transactionRecord: TransactionRecord) =
        settleActionController.acceptSettleActionTransaction(settleAction, transactionRecord)
    fun getAllSettleActionsAsFlow() = settleActionController.getAllSettleActionsAsFlow()

    object Images {
        fun getProfilePictureURI(userId: String) = "https://$AWS_IMAGES_BUCKET_NAME.s3.amazonaws.com/pfp_$userId.png"
    }

    suspend fun logOut() {
        subscriptionsJob.cancel()
        unloadKoinModules(
                module {
                    single { realm }
                }
        )
        unloadKoinModules(releaseAppModules)
        realm.subscriptions.update {
            removeAll()
        }
        Notifications.unsubscribeAllNotifications()
        realmUser.logOut()
    }
}