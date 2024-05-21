package com.grup.dbmanager

import com.grup.device.DeviceManager
import com.grup.device.SettingsManager
import com.grup.di.realmModules
import com.grup.exceptions.login.NotLoggedInException
import com.grup.models.BaseEntity
import com.grup.models.realm.RealmDebtAction
import com.grup.models.realm.RealmGroup
import com.grup.models.realm.RealmGroupInvite
import com.grup.models.realm.RealmSettleAction
import com.grup.models.realm.RealmTransactionRecord
import com.grup.models.realm.RealmUser
import com.grup.models.realm.RealmUserInfo
import com.grup.other.APP_ID
import com.grup.other.TEST_APP_ID
import com.grup.other.getLatest
import com.grup.other.idSerialName
import com.grup.platform.signin.AuthManager
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.AppConfiguration
import io.realm.kotlin.mongodb.AuthenticationProvider
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.asQuery
import io.realm.kotlin.mongodb.syncSession
import io.realm.kotlin.types.BaseRealmObject
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import kotlin.jvm.JvmStatic
import kotlin.time.Duration.Companion.seconds

internal open class RealmManager protected constructor(
    private val isDebug: Boolean = false
) : DatabaseManager(), KoinComponent {
    protected companion object {
        @JvmStatic
        protected val releaseApp: App = App.create(
            AppConfiguration.Builder(APP_ID)
                .baseUrl("https://services.cloud.mongodb.com")
                .enableSessionMultiplexing(true)
                .build()
        )
        @JvmStatic
        protected val debugApp: App = App.create(
            AppConfiguration.Builder(TEST_APP_ID)
                .baseUrl("https://services.cloud.mongodb.com")
                .enableSessionMultiplexing(true)
                .build()
        )
    }

    private val app: App
        get() = if (isDebug) debugApp else releaseApp

    override val authProvider: AuthManager.AuthProvider
        get() = app.currentUser?.let { user ->
            when (user.identities.first().provider) {
                AuthenticationProvider.GOOGLE -> AuthManager.AuthProvider.Google
                AuthenticationProvider.APPLE -> AuthManager.AuthProvider.Apple
                else -> AuthManager.AuthProvider.None
            }
        } ?: throw NotLoggedInException("Not logged into Realm")

    private val deviceManager: DeviceManager by inject()

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
                        "MyUserInfos")
                    add(
                        realm.query<RealmGroup>()
                    )
                    add(
                        realm.query<RealmGroupInvite>("inviterId == $0", realmUser.id),
                        "OutgoingGroupInvites"
                    )
                    add(
                        realm.query<RealmGroupInvite>("inviteeId == $0", realmUser.id),
                        "IncomingGroupInvites"
                    )
                }
                .name("syncedRealm_${realmUser.id}")
                .schemaVersion(0)
                .build()
        )
    } ?: throw NotLoggedInException()

    inner class RealmWriteTransaction(private val mutableRealm: MutableRealm) :
        DatabaseWriteTransaction(), MutableRealm by mutableRealm {
        override fun <T : BaseEntity> findObject(obj: T): T? = when(obj) {
            is BaseRealmObject -> getLatest(obj)
            else -> null
        }

        override fun cancelWrite() = mutableRealm.cancelWrite()
    }

    override suspend fun <T> write(transaction: DatabaseWriteTransaction.() -> T): T = realm.write {
        transaction(RealmWriteTransaction(this))
    }



    @OptIn(DelicateCoroutinesApi::class)
    private val groupSubscriptionsJob: Job = GlobalScope.launch(
        Dispatchers.Main,
        CoroutineStart.LAZY
    ) {
        val userInfosFlow: Flow<List<RealmUserInfo>> =
            realm.subscriptions.findByName("MyUserInfos")!!
                .asQuery<RealmUserInfo>().asFlow().map { it.list }

        // GroupIds
        val groupIdsFromUserInfos: Flow<List<String>> = userInfosFlow.map { userInfos ->
            userInfos.map { it.groupId }
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
                        realm.query<RealmUserInfo>("groupId == $0", groupId),
                        "${groupId}_UserInfo"
                    )
                    add(
                        realm.query<RealmDebtAction>("groupId == $0", groupId),
                        "${groupId}_DebtAction"
                    )
                    add(
                        realm.query<RealmSettleAction>("groupId == $0", groupId),
                        "${groupId}_SettleAction"
                    )
                    deviceManager.notificationManager.subscribeGroupNotifications(groupId)
                }
                prevSubscribedGroupIds.minus(newGroupIds).forEach { groupId ->
                    remove("${groupId}_Group")
                    remove("${groupId}_UserInfo")
                    remove("${groupId}_DebtAction")
                    remove("${groupId}_SettleAction")
                    deviceManager.notificationManager.unsubscribeGroupNotifications(groupId)
                }
            }
            prevSubscribedGroupIds = newGroupIds
        }
    }

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
            userInfos.map { it.userId }
        }
        val userIdsFromGroupInvitesInviters: Flow<List<String>> =
            groupInvitesFlow.map { groupInvites ->
                groupInvites.map { it.inviterId }
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
    private val groupOnlySubscriptionJob: Job = GlobalScope.launch(
        Dispatchers.Main,
        CoroutineStart.LAZY
    ) {
        val incomingGroupInvites: Flow<List<RealmGroupInvite>> =
            realm.subscriptions.findByName("IncomingGroupInvites")!!
                .asQuery<RealmGroupInvite>().asFlow().map { it.list }

        // GroupIds
        val groupIdsFromIncomingGroupInvite: Flow<List<String>> =
            incomingGroupInvites.map { groupInvites ->
                groupInvites.map { it.groupId }
            }

        var prevSubscribedGroupIds: Set<String> = emptySet()
        combine(
            groupIdsFromIncomingGroupInvite
        ) { allGroupInfos: Array<List<String>> ->
            allGroupInfos.flatMap { it }.toSet()
        }.collect { newGroupIds: Set<String> ->
            realm.subscriptions.update {
                newGroupIds.minus(prevSubscribedGroupIds).forEach { groupId ->
                    add(
                        realm.query<RealmGroup>("$idSerialName == $0", groupId),
                        "${groupId}_GroupOnly"
                    )
                }
                prevSubscribedGroupIds.minus(newGroupIds).forEach { groupId ->
                    remove("${groupId}_GroupOnly")
                }
            }
            prevSubscribedGroupIds = newGroupIds
        }
    }

    suspend fun open() {
        app.currentUser?.let { realmUser ->
            deviceManager.notificationManager.subscribePersonalNotifications(realmUser.id)
            SettingsManager.LoginSettings.userId = realmUser.id
        } ?: throw NotLoggedInException()
        groupSubscriptionsJob.start()
        userSubscriptionsJob.start()
        realm.subscriptions.waitForSynchronization(0.5.seconds)
        realm.syncSession.downloadAllServerChanges(0.5.seconds)
        groupOnlySubscriptionJob.start()
        loadKoinModules(realmModules(realm, isDebug))
    }

    override suspend fun logOut() {
        app.currentUser?.apply {
            this.logOut()
            closeRealmManager()
        } ?: throw NotLoggedInException()
    }

    override suspend fun deleteUser() {
        app.currentUser?.apply {
            closeRealmManager()
            realm.close()
            this.delete()
        } ?: throw NotLoggedInException()
    }

    private fun closeRealmManager() {
        userSubscriptionsJob.cancel()
        groupSubscriptionsJob.cancel()
        groupOnlySubscriptionJob.cancel()
        deviceManager.notificationManager.unsubscribeAllNotifications()
        unloadKoinModules(realmModules(realm, isDebug))
    }
}