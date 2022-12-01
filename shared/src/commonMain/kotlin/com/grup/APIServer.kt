package com.grup

import com.grup.controllers.GroupController
import com.grup.controllers.TransactionRecordController
import com.grup.controllers.UserController
import com.grup.di.createSyncedRealmModule
import com.grup.di.registerUserObject
import com.grup.di.repositoriesModule
import com.grup.di.servicesModule
import com.grup.di.user
import com.grup.exceptions.EntityAlreadyExistsException
import com.grup.models.Group
import com.grup.models.User
import com.grup.other.Id
import com.grup.other.RealmUser
import com.grup.repositories.UserRepository
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin

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
            return loginAnonymous().also { apiServer ->
                apiServer.registerUser(username)
            }
        }

        fun registerEmailPassword(email: String, password: String, username: String): APIServer {
            try {
                runBlocking {
                    app.emailPasswordAuth.registerUser(email, password)
                }
            } catch (e: UserAlreadyExistsException) {
                throw EntityAlreadyExistsException("Email already exists")
            }
            return loginWithEmailPassword(email, password).also { apiServer ->
                apiServer.registerUser(username)
            }
        }

        fun loginWithEmailPassword(email: String, password: String): APIServer {
//            try {
                return APIServer(Credentials.emailPassword(email, password))
//            } catch (e: Exception) {
//                // TODO: INVALID EMAIL PASS EXCEPTION
//                throw Exception()
//            }
        }
    }

    private val realmUser: RealmUser = runBlocking {
        app.login(credentials)
    }

    private fun registerUser(username: String) {
        registerUserObject(User(realmUser.id).apply {
            this.username = username
        })
    }

    init {
        startKoin {
            modules(listOf(
                createSyncedRealmModule(realmUser),
                servicesModule,
                repositoriesModule
            ))
        }
    }

    private val userController: UserController = UserController()
    private val groupController: GroupController = GroupController()
    private val transactionRecordController: TransactionRecordController = TransactionRecordController()

    fun createGroup(groupName: String) = groupController.createGroup(groupName, user)
    fun getGroupById(groupId: Id) = groupController.getGroupById(groupId)
    fun getAllGroupsAsFlow() = groupController.getAllGroupsAsFlow()
    // TODO: NEED TO ADD BY USERNAME VIA USER SERVICE
    fun addUserToGroup(group: Group, user: User) =
        groupController.addUserToGroup(user, group)

    fun logOut() {
        runBlocking {
            realmUser.logOut()
        }
    }
}
