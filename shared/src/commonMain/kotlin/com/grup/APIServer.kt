package com.grup

import com.grup.controllers.GroupController
import com.grup.controllers.PendingRequestController
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
import com.grup.models.PendingRequest
import com.grup.models.User
import com.grup.other.Id
import com.grup.other.RealmUser
import com.grup.repositories.APP_ID
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin

object APIServer {
    private val app: App = App.create(APP_ID)

    private val realmUser: RealmUser
        get() = app.currentUser ?: throw NotLoggedInException()

    val user: User
        get() = realm.query<User>().first().find() ?: throw UserObjectNotFoundException()

    fun createGroup(groupName: String) = GroupController.createGroup(groupName, user)
    fun getGroupById(groupId: Id) = GroupController.getGroupById(groupId)
    fun getAllGroupsAsFlow() = GroupController.getAllGroupsAsFlow()
    fun inviteUserToGroup(username: String, group: Group) =
        GroupController.inviteUserToGroup(username, group)
    fun testInviteUser(group: Group) = GroupController.inviteUserTest(user, group)
    fun acceptInviteToGroup(pendingRequest: PendingRequest) =
        GroupController.acceptInviteToGroup(pendingRequest, user)
    fun getAllPendingRequestsAsFlow() = PendingRequestController.getAllPendingRequestsAsFlow()

    fun getUserByUsername(username: String) = UserController.getUserByUsername(username)

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

    fun registerUser(username: String) {
        registerUserObject(User().apply {
            this._id = realmUser.id
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
