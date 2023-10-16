package com.grup.di

import com.grup.exceptions.MissingFieldException
import com.grup.exceptions.login.NotLoggedInException
import com.grup.interfaces.DBManager
import com.grup.models.realm.RealmDebtAction
import com.grup.models.realm.RealmGroup
import com.grup.models.realm.RealmGroupInvite
import com.grup.models.realm.RealmSettleAction
import com.grup.models.realm.RealmTransactionRecord
import com.grup.models.realm.RealmUser
import com.grup.models.realm.RealmUserInfo
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
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.dsl.module
import kotlin.jvm.JvmStatic
import kotlin.time.Duration.Companion.seconds

internal open class RealmManager(private val isDebug: Boolean = false) : DBManager {
    protected companion object {
        @JvmStatic
        protected val releaseApp: App = App.create(APP_ID)
        @JvmStatic
        protected val debugApp: App = App.create(TEST_APP_ID)
    }

    private val app: App
        get() = if (isDebug) debugApp else releaseApp

    override val authProvider: AuthManager.AuthProvider
        get() = app.currentUser?.let { user ->
            when (user.provider) {
                AuthenticationProvider.GOOGLE -> AuthManager.AuthProvider.Google
                AuthenticationProvider.APPLE -> AuthManager.AuthProvider.Apple
                else -> AuthManager.AuthProvider.None
            }
        } ?: throw NotLoggedInException("Not logged into Realm")

    override suspend fun startDBTransaction(transaction: () -> Unit) = realm.write {
        transaction()
    }


    private val realm: Realm = app.currentUser?.let { realmUser ->
        Realm.open(
            SyncConfiguration.Builder(
                realmUser,
                setOf(
                    RealmUser::class, RealmGroup::class, RealmUserInfo::class,
                    RealmGroupInvite::class, RealmDebtAction::class, RealmSettleAction::class,
                    RealmTransactionRecord::class
                )
            )
                .initialSubscriptions(rerunOnOpen = true) { realm ->
                    removeAll()
                    add(realm.query<RealmUser>("$idSerialName == $0", realmUser.id), "MyUser")
                    add(
                        realm.query<RealmUserInfo>("userId == $0", realmUser.id),
                        "UserInfos")
                    add(
                        realm.query<RealmGroupInvite>("inviterId == $0", realmUser.id),
                        "OutgoingGroupInvites"
                    )
                    add(
                        realm.query<RealmGroupInvite>("inviteeId == $0", realmUser.id),
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
    private val userSubscriptionsJob: Job = GlobalScope.launch(
        Dispatchers.Main,
        CoroutineStart.LAZY
    ) {
        val userInfosFlow: Flow<List<RealmUserInfo>> =
            realm.query<RealmUserInfo>().asFlow().map { it.list }
        val groupInvitesFlow: Flow<List<RealmGroupInvite>> =
            realm.subscriptions.findByName("IncomingGroupInvites")!!
                .asQuery<RealmGroupInvite>().asFlow().map { it.list }

        val userIdsFromUserInfos: Flow<List<String>> = userInfosFlow.map { userInfos ->
            userInfos.map { it._userId ?: throw MissingFieldException() }
        }
        val userIdsFromGroupInvitesInviters: Flow<List<String>> =
            groupInvitesFlow.map { groupInvites ->
                groupInvites.map { it._inviterId ?: throw MissingFieldException() }
            }

        var prevSubscribedUserIds: Set<String> = emptySet()
        combine(
            userIdsFromUserInfos,
            userIdsFromGroupInvitesInviters
        ) { allUserIds: Array<List<String>> ->
            allUserIds.flatMap { it }.toSet()
        }.collect { newUserIds: Set<String> ->
            realm.subscriptions.update {
                newUserIds.minus(prevSubscribedUserIds).forEach { userId ->
                    add(realm.query<RealmUser>("$idSerialName == $0", userId),
                        "${userId}_User")
                }
                prevSubscribedUserIds.minus(newUserIds).forEach { userId ->
                    remove("${userId}_User")
                }
            }
            prevSubscribedUserIds = newUserIds
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private val groupSubscriptionsJob: Job = GlobalScope.launch(
        Dispatchers.Main,
        CoroutineStart.LAZY
    ) {
        val userInfosFlow: Flow<List<RealmUserInfo>> =
            realm.subscriptions.findByName("UserInfos")!!
                .asQuery<RealmUserInfo>().asFlow().map { it.list }

        // GroupIds
        val groupIdsFromUserInfos: Flow<List<String>> = userInfosFlow.map { userInfos ->
            userInfos.map { it._groupId ?: throw MissingFieldException() }
        }

        var prevSubscribedGroupIds: Set<String> = emptySet()
        combine(
            groupIdsFromUserInfos
        ) { allGroupInfos: Array<List<String>> ->
            allGroupInfos.flatMap { it }.toSet()
        }.collect { newGroupIds: Set<String> ->
            realm.subscriptions.update {
                newGroupIds.minus(prevSubscribedGroupIds).forEach { groupId ->
                    add(
                        realm.query<RealmGroup>("$idSerialName == $0", groupId),
                        "${groupId}_Group"
                    )
                    add(
                        realm.query<RealmUserInfo>("_groupId == $0", groupId),
                        "${groupId}_UserInfo"
                    )
                    add(
                        realm.query<RealmDebtAction>("_groupId == $0", groupId),
                        "${groupId}_DebtAction"
                    )
                    add(
                        realm.query<RealmSettleAction>("_groupId == $0", groupId),
                        "${groupId}_SettleAction"
                    )
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
        app.currentUser?.let { realmUser ->
            NotificationsService.subscribePersonalNotifications(realmUser.id)
        } ?: throw NotLoggedInException()
        realm.subscriptions.waitForSynchronization(5.seconds)
        realm.syncSession.downloadAllServerChanges(5.seconds)
        userSubscriptionsJob.start()
        groupSubscriptionsJob.start()
        loadKoinModules(if (isDebug) debugAppModule else releaseAppModule)
    }

    override suspend fun close() {
        app.currentUser?.apply { logOut() } ?: throw NotLoggedInException()
        realm.close()
        userSubscriptionsJob.cancel()
        groupSubscriptionsJob.cancel()
        NotificationsService.unsubscribeAllNotifications()
        unloadKoinModules(
            listOf(
                module {
                    single { realm }
                },
                if (isDebug) debugAppModule else releaseAppModule
            )
        )
    }
}