package com.grup

import com.grup.controllers.GroupController
import com.grup.controllers.TransactionRecordController
import com.grup.controllers.UserController
import com.grup.di.servicesModule
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.ITransactionRecordRepository
import com.grup.interfaces.IUserRepository
import com.grup.models.Group
import com.grup.models.User
import com.grup.repositories.*
import com.grup.repositories.LoggedInUserManager
import com.grup.repositories.SyncedTransactionRecordRepository
import com.grup.repositories.UserRepository
//import com.grup.repositories.SyncedGroupRepository
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import kotlinx.coroutines.runBlocking
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import org.koin.dsl.module


typealias RealmUser = io.realm.kotlin.mongodb.User

class APIServer private constructor(
    private val credentials: Credentials = Credentials.anonymous()
) {
    companion object {
        private val app: App = App.create("")
        private val userRepository: UserRepository by lazy { UserRepository() }

        fun verifyUsername(username: String): Boolean {
            return userRepository.findUserByUserName(username) == null
        }

        fun loginAnonymous(): APIServer {
            return APIServer()
        }

        fun registerAnonymous(username: String): APIServer {
            return loginAnonymous().registerUser(username)
        }

        fun registerEmailPassword(email: String, password: String, username: String): APIServer {
            runBlocking {
                app.emailPasswordAuth.registerUser(email, password)
            }
            return loginWithEmailPassword(email, password).registerUser(username)
        }

        fun loginWithEmailPassword(email: String, password: String): APIServer {
            return APIServer(Credentials.emailPassword(email, password))
        }

        private fun APIServer.registerUser(username: String): APIServer {
            return this.apply {
                this.userManager.registerUser(
                    User().apply {
                        this.username = username
                    }
                )
            }
        }
    }

    private val user: RealmUser = runBlocking {
        app.login(credentials)
    }

    private val userManager: LoggedInUserManager = LoggedInUserManager(user)

    init {
        startKoin {
            modules(listOf(
                // Regular Repositories
                module {
                    single<IUserRepository> { userRepository }
                },
                // Synced Repositories
                module {
                    single<IGroupRepository> { SyncedGroupRepository(userManager) }
                    single<ITransactionRecordRepository> {
                        SyncedTransactionRecordRepository(userManager)
                    }
                },
                servicesModule
            ))
        }
    }

    private val userController: UserController = UserController()
    private val groupController: GroupController = GroupController()
    private val transactionRecordController: TransactionRecordController = TransactionRecordController()

    fun createGroup(groupName: String) = groupController.createGroup(groupName)
    fun findGroupById(groupId: String) = groupController.getGroupById(groupId)
    fun addUserToGroup(group: Group, username: String) =
        groupController.addUserToGroup(group, username)

    fun logOut() {
        runBlocking {
            user.logOut()
        }
    }
}
