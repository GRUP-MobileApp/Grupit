package com.grup

import com.grup.controllers.GroupController
import com.grup.controllers.UserController
import com.grup.di.createSyncedRealmModule
import com.grup.di.realm
import com.grup.di.registerUserObject
import com.grup.di.repositoriesModule
import com.grup.di.servicesModule
import com.grup.exceptions.EntityAlreadyExistsException
import com.grup.exceptions.login.InvalidEmailPasswordException
import com.grup.exceptions.login.NotLoggedInException
import com.grup.exceptions.login.UserObjectNotFoundException
import com.grup.models.Group
import com.grup.models.User
import com.grup.other.Id
import com.grup.other.RealmUser
import com.grup.repositories.API_KEY
import com.grup.repositories.UserRepository
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin

object APIServer {
    private val app: App = App.create(API_KEY)
    private val userRepository: UserRepository by lazy { UserRepository() }

    val realmUser: RealmUser
        get() = app.currentUser ?: throw NotLoggedInException()

    val user: User
        get() = realm.query<User>().first().find() ?: throw UserObjectNotFoundException()

    fun testVerifyUsername(username: String) = UserController.getUserByUsername(username)
    fun createGroup(groupName: String) = GroupController.createGroup(groupName, user)
    fun getGroupById(groupId: Id) = GroupController.getGroupById(groupId)
    fun getAllGroupsAsFlow() = GroupController.getAllGroupsAsFlow()
    // TODO: NEED TO ADD BY USERNAME VIA USER SERVICE
    fun addUserToGroup(group: Group, user: User) =
        GroupController.addUserToGroup(user, group)

    object Login {
        private fun login(credentials: Credentials) {
            runBlocking {
                app.login(credentials)
            }
            initKoin()
        }

        fun emailAndPassword(email: String, password: String) {
            try {
                login(Credentials.emailPassword(email, password))
            } catch (e: InvalidCredentialsException) {
                throw InvalidEmailPasswordException()
            }
        }

        fun registerEmailAndPassword(email: String, password: String) {
            try {
                runBlocking {
                    app.emailPasswordAuth.registerUser(email, password)
                }
            } catch (e: UserAlreadyExistsException) {
                throw EntityAlreadyExistsException("Email already exists")
            }
            emailAndPassword(email, password)
        }
    }

    private fun verifyUsername(username: String): Boolean {
        return userRepository.findUserByUserName(username) == null
    }

    fun registerUser(username: String) {
        registerUserObject(User(realmUser.id).apply {
            this.username = username
        })
    }

    fun logOut() {
        runBlocking {
            realmUser.logOut()
        }
    }


    private fun initKoin() {
        startKoin {
            modules(listOf(
                createSyncedRealmModule(realmUser),
                servicesModule,
                repositoriesModule
            ))
        }
    }
}
