package com.grup

import com.grup.controllers.GroupController
import com.grup.controllers.GroupInviteController
import com.grup.controllers.UserController
import com.grup.controllers.UserInfoController
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
import com.grup.models.GroupInvite
import com.grup.models.User
import com.grup.other.RealmUser
import com.grup.repositories.APP_ID
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.Credentials
import io.realm.kotlin.mongodb.exceptions.BadRequestException
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.context.startKoin

object APIServer {
    private val app: App = App.create(APP_ID)

    private val realmUser: RealmUser
        get() = app.currentUser ?: throw NotLoggedInException()

    val user: User
        get() = realm.query<User>().first().find() ?: throw UserObjectNotFoundException()

    // User
    fun getUserByUsername(username: String) = UserController.getUserByUsername(username)

    // Group
    fun createGroup(groupName: String) = GroupController.createGroup(user, groupName)
    fun getAllGroupsAsFlow() = GroupController.getAllGroupsAsFlow()

    // UserInfo
    fun getUserInfosByGroupIdAsFlow(group: Group) =
        UserInfoController.getUserInfosByGroupIdAsFlow(group)

    // GroupInvite
    fun inviteUserToGroup(username: String, group: Group) =
        GroupInviteController.createGroupInvite(user, username, group)
    fun acceptInviteToGroup(groupInvite: GroupInvite) =
        GroupInviteController.acceptInviteToGroup(groupInvite, user)
    fun getAllGroupInvitesAsFlow() = GroupInviteController.getAllGroupInvitesAsFlow()


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
            }
        }

        suspend fun registerEmailAndPassword(email: String, password: String) {
            try {
                app.emailPasswordAuth.registerUser(email, password)
            } catch (e: UserAlreadyExistsException) {
                throw EntityAlreadyExistsException("Email already exists")
            } catch (e: BadRequestException) {
                // TODO: Bad email/bad password exception
            }
            emailAndPassword(email, password)
        }
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

    private suspend fun initKoin() {
        val realmModule = createSyncedRealmModule(realmUser)
        startKoin {
            modules(listOf(
                realmModule,
                servicesModule,
                repositoriesModule
            ))
        }
    }
}