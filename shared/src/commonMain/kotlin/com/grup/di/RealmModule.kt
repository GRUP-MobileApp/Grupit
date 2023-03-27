package com.grup.di

import com.grup.APIServer
import com.grup.models.*
import com.grup.other.APP_ID
import com.grup.other.RealmUser
import com.grup.other.idSerialName
import com.grup.service.Notifications
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.App
import io.realm.kotlin.mongodb.sync.MutableSubscriptionSet
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.mongodb.sync.asQuery
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.*

internal suspend fun openSyncedRealm(realmUser: RealmUser): Realm {
    return Realm.open(
        SyncConfiguration.Builder(realmUser,
            setOf(
                User::class, Group::class, UserInfo::class, GroupInvite::class, DebtAction::class,
                SettleAction::class, TransactionRecord::class
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
        .name("user${realmUser.id}_SyncedRealm")
        .build()
    ).also { realm ->
        realm.syncSession.downloadAllServerChanges()
        Notifications.subscribePersonalNotifications(realmUser.id)
    }
}

@OptIn(DelicateCoroutinesApi::class)
internal fun APIServer.startSubscriptionSyncJob(): Job = GlobalScope.launch {
    var prevSubscribedGroupIds: Set<String> = emptySet()
    realm.subscriptions.findByName("UserInfos")?.asQuery<UserInfo>()!!.asFlow()
        .collect { resultsChange ->
            val newGroupIds: Set<String> = resultsChange.list.map { it.groupId!! }.toSet()

            realm.subscriptions.update {
                prevSubscribedGroupIds.minus(newGroupIds).forEach { groupId ->
                    this.removeGroup(groupId)
                }
                newGroupIds.minus(prevSubscribedGroupIds).forEach { groupId ->
                    this.addGroup(it, groupId)
                }
            }
            prevSubscribedGroupIds = newGroupIds
        }
}

internal fun MutableSubscriptionSet.addGroup(realm: Realm, groupId: String) {
    add(realm.query<Group>("$idSerialName == $0", groupId), "${groupId}_Group")
    add(realm.query<UserInfo>("groupId == $0", groupId), "${groupId}_UserInfo")
    add(realm.query<DebtAction>("groupId == $0", groupId),
        "${groupId}_DebtAction")
    add(realm.query<SettleAction>("groupId == $0", groupId),
        "${groupId}_SettleAction")
    Notifications.subscribeGroupNotifications(groupId)
}

internal fun MutableSubscriptionSet.removeGroup(groupId: String) {
    remove("${groupId}_Group")
    remove("${groupId}_UserInfo")
    remove("${groupId}_DebtAction")
    remove("${groupId}_SettleAction")
    Notifications.unsubscribeGroupNotifications(groupId)
}

internal fun <T: BaseEntity> MutableRealm.getLatestFields(obj: T): T {
    return when(obj) {
        is SettleAction -> {
            obj.debteeUserInfo = findLatest(obj.debteeUserInfo!!)
            obj.transactionRecords.forEachIndexed { i, _ ->
                obj.transactionRecords[i].apply {
                    this.debtorUserInfo = findLatest(this.debtorUserInfo!!)!!
                }
            }
            obj
        }
        is DebtAction -> {
            obj.debteeUserInfo = findLatest(obj.debteeUserInfo!!)
            obj.transactionRecords.forEachIndexed { i, _ ->
                obj.transactionRecords[i].apply {
                    this.debtorUserInfo = findLatest(this.debtorUserInfo!!)!!
                }
            }
            obj
        }
        else -> obj
    }
}
