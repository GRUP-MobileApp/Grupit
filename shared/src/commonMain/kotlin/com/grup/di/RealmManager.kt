package com.grup.di

import com.grup.exceptions.login.NotLoggedInException
import com.grup.interfaces.DBManager
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.other.APP_ID
import com.grup.other.TEST_APP_ID
import com.grup.other.idSerialName
import com.grup.platform.signin.AuthManager
import com.grup.service.NotificationsService
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AuthenticationProvider
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.asQuery
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import kotlin.jvm.JvmStatic
import kotlin.time.Duration.Companion.seconds

abstract class RealmManager(private val isDebug: Boolean = false) : DBManager {
    private val app: App
        get() = (if (isDebug) debugApp else releaseApp)

    override val authProvider: AuthManager.AuthProvider
        get() = app.currentUser?.let { user ->
            when (user.provider) {
                AuthenticationProvider.GOOGLE -> AuthManager.AuthProvider.Google
                AuthenticationProvider.APPLE -> AuthManager.AuthProvider.Apple
                else -> AuthManager.AuthProvider.None
            }
        } ?: throw NotLoggedInException("Not logged into Realm")


    private val realm: Realm = app.currentUser?.let { realmUser ->
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
                    add(realm.query<User>("$idSerialName == $0", realmUser.id), "MyUser")
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
                .name("syncedRealm")
                .build()
        ).also { realm ->
            loadKoinModules(
                module {
                    single { realm }
                }
            )
        }
    } ?: throw NotLoggedInException()

    @OptIn(DelicateCoroutinesApi::class)
    private val subscriptionsJob: Job = GlobalScope.launch(
        Dispatchers.Main,
        CoroutineStart.LAZY
    ) {
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
                        NotificationsService.subscribeGroupNotifications(groupId)
                    }
                    prevSubscribedGroupIds.minus(newGroupIds).forEach { groupId ->
                        remove("${groupId}_Group")
                        remove("${groupId}_UserInfo")
                        remove("${groupId}_DebtAction")
                        remove("${groupId}_SettleAction")
                        NotificationsService.unsubscribeGroupNotifications(groupId)
                    }
                }
                prevSubscribedGroupIds = newGroupIds
            }
    }

    suspend fun open() {
        realm.syncSession.downloadAllServerChanges(5.seconds)
        subscriptionsJob.start()
        loadKoinModules(if (isDebug) debugAppModules else releaseAppModules)
    }

    companion object {
        @JvmStatic
        protected val releaseApp: App = App.create(APP_ID)
        @JvmStatic
        protected val debugApp: App = App.create(TEST_APP_ID)
    }

    override suspend fun close() {
        app.currentUser?.apply { logOut() } ?: throw NotLoggedInException()
        subscriptionsJob.cancel()
        NotificationsService.unsubscribeAllNotifications()
        unloadKoinModules(
            listOf(
                module {
                    single { realm }
                },
                if (isDebug) debugAppModules else releaseAppModules
            )
        )
    }
}