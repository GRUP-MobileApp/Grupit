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
import com.grup.other.idSerialName
import com.grup.platform.signin.AuthManager
import com.grup.service.Notifications
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AuthenticationProvider
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.asQuery
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import kotlin.jvm.JvmStatic
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime

abstract class RealmManager : DBManager, KoinComponent {
    protected val realm: Realm by inject()

    @OptIn(DelicateCoroutinesApi::class)
    protected val subscriptionsJob: Job = GlobalScope.launch {
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
        @ExperimentalTime
        @JvmStatic
        protected suspend fun openRealm(realmUser: io.realm.kotlin.mongodb.User) {
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
                    .name("syncedRealm")
                    .build()
            ).also { realm ->
                realm.syncSession.downloadAllServerChanges(5.seconds)
                loadKoinModules(
                    module {
                        single { realm }
                    }
                )
            }
        }
    }

    override suspend fun close() {
        subscriptionsJob.cancel()
        unloadKoinModules(
            module {
                single { realm }
            }
        )
        Notifications.unsubscribeAllNotifications()
    }

    protected fun getAuthProvider(app: App): AuthManager.AuthProvider =
        app.currentUser?.let { user ->
            when (user.provider) {
                AuthenticationProvider.GOOGLE -> AuthManager.AuthProvider.Google
                AuthenticationProvider.APPLE -> AuthManager.AuthProvider.Apple
                else -> AuthManager.AuthProvider.None
            }
        } ?: throw NotLoggedInException("Not logged into Realm")
}