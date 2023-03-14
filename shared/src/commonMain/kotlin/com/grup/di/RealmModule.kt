package com.grup.di

import com.grup.APIServer
import com.grup.models.*
import com.grup.other.RealmUser
import com.grup.other.idSerialName
import com.grup.service.Notifications
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.sync.MutableSubscriptionSet
import io.realm.kotlin.mongodb.sync.SyncConfiguration
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
        .initialSubscriptions { realm ->
            removeAll()
            add(realm.query<User>("$idSerialName == $0", realmUser.id), "User")
            add(realm.query<UserInfo>("userId == $0", realmUser.id), "UserInfos")
            add(
                realm.query<GroupInvite>("inviter == $0 OR invitee == $0", realmUser.id),
                "GroupInvites"
            )
        }
        .name("user${realmUser.id}_SyncedRealm")
        .build()
    ).also { realm ->
        realm.syncSession.downloadAllServerChanges()
        Notifications.subscribePersonalNotifications(realmUser.id)
    }
}

@OptIn(DelicateCoroutinesApi::class)
internal fun APIServer.startSubscriptionSyncJob(): Job = GlobalScope.launch {
    realm.query<UserInfo>().find().asFlow().collect { resultsChange ->
        realm.subscriptions.update {
            val currentUserInfoGroupIds = resultsChange.list.map { it.groupId!! }
            val currentSubscribedGroupIds = realm.subscriptions.toList().filter {
                it.name?.endsWith("_Group") ?: false
            }.map { it.name!!.removeSuffix("_Group") }
            currentSubscribedGroupIds.minus(currentUserInfoGroupIds.toSet()).forEach { groupId ->
                this.removeGroup(groupId)
                println("removing $groupId")
            }
            currentUserInfoGroupIds.minus(currentSubscribedGroupIds.toSet()).forEach { groupId ->
                println("adding $groupId")
                this.addGroup(realm, groupId)
            }
        }
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
