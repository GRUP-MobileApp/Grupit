package com.grup.di

import com.grup.models.*
import com.grup.other.RealmUser
import com.grup.other.idSerialName
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.sync.MutableSubscriptionSet
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import kotlinx.coroutines.*

internal lateinit var realm: Realm
private lateinit var subscriptionsJob: Job

internal suspend fun openSyncedRealm(realmUser: RealmUser): Realm {
    realm = Realm.open(
        SyncConfiguration.Builder(realmUser,
            setOf(
                User::class, Group::class, UserInfo::class, GroupInvite::class, DebtAction::class,
                TransactionRecord::class
            )
        )
        .initialSubscriptions(rerunOnOpen = true) { realm ->
            removeAll()
            add(realm.query<User>("$idSerialName == $0", realmUser.id), "User")
            add(realm.query<UserInfo>("userId == $0", realmUser.id), "UserInfos")
            add(
                realm.query<GroupInvite>("inviter == $0 OR invitee == $0", realmUser.id),
                "GroupInvites"
            )
        }
        .waitForInitialRemoteData()
        .name("syncedRealm")
        .build()
    )
    subscriptionsJob = subscriptionsSyncGlobalJob()
    realm.subscriptions.waitForSynchronization()
    return realm
}

internal fun closeSyncedRealm() {
    realm.close()
    subscriptionsJob.cancel()
}

@OptIn(DelicateCoroutinesApi::class)
fun subscriptionsSyncGlobalJob(): Job = GlobalScope.launch {
    var prevUserInfoList: List<UserInfo> = emptyList()
    realm.query<UserInfo>().find().asFlow().collect { resultsChange ->
        realm.subscriptions.update {
            resultsChange.list.minus(prevUserInfoList.toSet()).forEach { userInfo ->
                this.addGroup(userInfo.groupId!!)
            }
        }
        prevUserInfoList = resultsChange.list
    }
}

internal fun registerUserObject(newUser: User) {
    realm.writeBlocking {
        copyToRealm(newUser)
    }
}

internal fun MutableSubscriptionSet.addGroup(groupId: String) {
    add(realm.query<Group>("$idSerialName == $0", groupId), "${groupId}_Group")
    add(realm.query<DebtAction>("groupId == $0", groupId),
        "${groupId}_DebtAction")
}

internal fun MutableSubscriptionSet.removeGroup(groupId: String) {
    remove("${groupId}_Group")
    remove("${groupId}_DebtAction")
}
