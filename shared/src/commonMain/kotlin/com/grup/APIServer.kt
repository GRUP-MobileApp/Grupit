package com.grup

import com.grup.controllers.*
import com.grup.di.httpClientModule
import com.grup.di.openSyncedRealm
import com.grup.di.realm
import com.grup.di.registerUserObject
import com.grup.di.repositoriesModule
import com.grup.di.servicesModule
import com.grup.di.stopSubscriptionSyncJob
import com.grup.exceptions.EntityAlreadyExistsException
import com.grup.exceptions.login.InvalidEmailPasswordException
import com.grup.exceptions.login.NotLoggedInException
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.models.*
import com.grup.other.RealmUser
import com.grup.repositories.APP_ID
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.exceptions.BadRequestException
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

object APIServer {
    private val app: App = App.create(APP_ID)

    private val realmUser: RealmUser
        get() = app.currentUser ?: throw NotLoggedInException()

    val user: User
        get() = realm.query<User>().first().find() ?: throw UserObjectNotFoundException()

    // User
    suspend fun usernameExists(username: String) = UserController.usernameExists(username)

    // Group
    fun createGroup(groupName: String) = GroupController.createGroup(user, groupName)
    fun getAllGroupsAsFlow() = GroupController.getAllGroupsAsFlow()

    // UserInfo
    fun getMyUserInfosAsFlow() = UserInfoController.getMyUserInfosAsFlow(user)
    fun getAllUserInfosAsFlow() = UserInfoController.getAllUserInfosAsFlow()

    // GroupInvite
    suspend fun inviteUserToGroup(username: String, group: Group) =
        GroupInviteController.createGroupInvite(user, username, group)
    fun acceptInviteToGroup(groupInvite: GroupInvite) =
        GroupInviteController.acceptInviteToGroup(groupInvite, user)
    fun getAllGroupInvitesAsFlow() = GroupInviteController.getAllGroupInvitesAsFlow()

    // DebtAction
    fun createDebtAction(
        transactionRecords: List<TransactionRecord>,
        debtee: UserInfo,
        message: String
    ) = DebtActionController.createDebtAction(transactionRecords, debtee, message)
    fun acceptDebtAction(debtAction: DebtAction, myTransactionRecord: TransactionRecord) =
        DebtActionController.acceptDebtAction(debtAction, myTransactionRecord)
    fun getAllDebtActionsAsFlow() = DebtActionController.getAllDebtActionsAsFlow()

    // SettleAction
    fun createSettleAction(settleAmount: Double, debtee: UserInfo) =
        SettleActionController.createSettleAction(settleAmount, debtee)
    fun createSettleActionTransaction(
        settleAction: SettleAction,
        myTransactionRecord: TransactionRecord
    ) = SettleActionController.createSettleActionTransaction(settleAction, myTransactionRecord)
    fun acceptSettleActionTransaction(settleAction: SettleAction,
                                      transactionRecord: TransactionRecord) =
        SettleActionController.acceptTransactionRecord(settleAction, transactionRecord)
    fun getAllSettleActionsAsFlow() = SettleActionController.getAllSettleActionsAsFlow()


    object Login {
        private suspend fun login(credentials: Credentials) {
            app.login(credentials)
            initKoin()
        }

        suspend fun emailAndPassword(email: String, password: String) {
            try {
                login(Credentials.emailPassword(email, password))
            } catch (e: InvalidCredentialsException) {
                throw InvalidEmailPasswordException()
            } catch (e: IllegalArgumentException) {
                throw InvalidEmailPasswordException(e.message)
            }
        }

        suspend fun registerEmailAndPassword(email: String, password: String) {
            try {
                app.emailPasswordAuth.registerUser(email, password)
            } catch (e: UserAlreadyExistsException) {
                throw EntityAlreadyExistsException("Email already exists")
            } catch (e: IllegalArgumentException) {
                throw InvalidEmailPasswordException(e.message)
            } catch (e: BadRequestException) {
                // TODO: Bad email/bad password exception
            }
            emailAndPassword(email, password)
        }
    }

    fun registerUser(username: String, displayName: String) {
        registerUserObject(
            User(realmUser.id).apply {
                this.username = username
                this.displayName = displayName
            }
        )
    }

    fun logOut() {
        stopSubscriptionSyncJob()
        stopKoin()
        runBlocking {
            realmUser.logOut()
        }
    }

    private suspend fun initKoin() {
        val realm = openSyncedRealm(realmUser)
        startKoin {
            modules(listOf(
                module {
                    single { realm }
                },
                servicesModule,
                repositoriesModule,
                httpClientModule
            ))
        }
    }
}