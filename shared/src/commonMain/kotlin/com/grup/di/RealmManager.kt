package com.grup.di

import com.grup.exceptions.EntityAlreadyExistsException
import com.grup.exceptions.login.InvalidEmailPasswordException
import com.grup.exceptions.login.InvalidGoogleAccountException
import com.grup.exceptions.login.NotLoggedInException
import com.grup.models.*
import com.grup.models.User
import com.grup.other.RealmUser
import com.grup.other.idSerialName
import com.grup.interfaces.DBManager
import com.grup.other.APP_ID
import com.grup.service.Notifications
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.*
import io.realm.kotlin.mongodb.exceptions.AuthException
import io.realm.kotlin.mongodb.exceptions.BadRequestException
import io.realm.kotlin.mongodb.exceptions.InvalidCredentialsException
import io.realm.kotlin.mongodb.exceptions.UserAlreadyExistsException
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.asQuery
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module

internal class RealmManager : KoinComponent, DBManager {
    private val realm: Realm by inject()

    @OptIn(DelicateCoroutinesApi::class)
    private val subscriptionsJob: Job = GlobalScope.launch {
        var prevSubscribedGroupIds: Set<String> = emptySet()
        realm.subscriptions.findByName("UserInfos")?.asQuery<UserInfo>()!!.asFlow()
            .collect { resultsChange ->
                val newGroupIds: Set<String> = resultsChange.list.map { it.groupId!! }.toSet()

                realm.subscriptions.update {
                    newGroupIds.minus(prevSubscribedGroupIds).forEach { groupId ->
                        add(realm.query<Group>("$idSerialName == $0", groupId),
                            "${groupId}_Group")
                        add(realm.query<UserInfo>("groupId == $0", groupId),
                            "${groupId}_UserInfo")
                        add(realm.query<DebtAction>("groupId == $0", groupId),
                            "${groupId}_DebtAction")
                        add(realm.query<SettleAction>("groupId == $0", groupId),
                            "${groupId}_SettleAction")
                        Notifications.subscribeGroupNotifications(groupId)
                    }
                    prevSubscribedGroupIds.minus(newGroupIds).forEach { groupId ->
                        remove("${groupId}_Group")
                        remove("${groupId}_UserInfo")
                        remove("${groupId}_DebtAction")
                        remove("${groupId}_SettleAction")
                        Notifications.unsubscribeGroupNotifications(groupId)
                    }
                }
                prevSubscribedGroupIds = newGroupIds
            }
    }

    companion object {
        private val app: App = App.create(APP_ID)

        private val realmUser: RealmUser
            get() = app.currentUser ?: throw NotLoggedInException()

        suspend fun loginGoogle(googleAccountToken: String): RealmManager {
            try {
                return loginRealmManager(
                    Credentials.google(googleAccountToken, GoogleAuthType.ID_TOKEN)
                )
            } catch (e: AuthException) {
                throw InvalidGoogleAccountException(e.message)
            }
        }

        suspend fun loginEmailPassword(email: String, password: String): RealmManager {
            try {
                return loginRealmManager(Credentials.emailPassword(email, password))
            } catch (e: InvalidCredentialsException) {
                throw InvalidEmailPasswordException()
            } catch (e: IllegalArgumentException) {
                throw InvalidEmailPasswordException(e.message)
            }
        }

        suspend fun registerEmailPassword(email: String, password: String): RealmManager {
            try {
                app.emailPasswordAuth.registerUser(email, password)
            } catch (e: UserAlreadyExistsException) {
                throw EntityAlreadyExistsException("Email already exists")
            } catch (e: IllegalArgumentException) {
                throw InvalidEmailPasswordException(e.message)
            } catch (e: BadRequestException) {
                // TODO: Bad email/bad password exception
            }
            return loginEmailPassword(email, password)
        }

        private suspend fun loginRealmManager(credentials: Credentials): RealmManager {
            app.login(credentials)
            Realm.open(
                SyncConfiguration.Builder(
                    realmUser,
                    setOf(
                        User::class, Group::class, UserInfo::class, GroupInvite::class,
                        DebtAction::class, SettleAction::class, TransactionRecord::class
                    )
                )
                    .initialSubscriptions(rerunOnOpen = true) { realm ->
                        removeAll()
                        add(realm.query<User>("$idSerialName == $0", realmUser.id), "User")
                        add(realm.query<UserInfo>("userId == $0", realmUser.id), "UserInfos")
                        add(
                            realm.query<GroupInvite>("inviter == $0", realmUser.id),
                            "OutgoingGroupInvites"
                        )
                        add(
                            realm.query<GroupInvite>("invitee == $0", realmUser.id),
                            "IncomingGroupInvites"
                        )
                    }
                    .waitForInitialRemoteData()
                    .name("syncedRealm")
                    .build()
            ).also { realm ->
                realm.syncSession.downloadAllServerChanges()
                loadKoinModules(
                    module {
                        single { realm }
                    }
                )
            }
            Notifications.subscribePersonalNotifications(realmUser.id)
            return RealmManager()
        }
    }

    override suspend fun close() {
        subscriptionsJob.cancel()
        unloadKoinModules(
            module {
                single { realm }
            }
        )
        unloadKoinModules(releaseAppModules)
        Notifications.unsubscribeAllNotifications()
        realmUser.logOut()
    }
}