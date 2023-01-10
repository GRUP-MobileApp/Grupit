package com.grup.di

import com.grup.models.*
import com.grup.other.RealmUser
import com.grup.other.idSerialName
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.subscriptions
import kotlinx.coroutines.runBlocking
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import org.koin.core.module.Module
import org.koin.dsl.module

internal lateinit var realm: Realm

internal suspend fun createSyncedRealmModule(realmUser: RealmUser): Module {
    realm = Realm.open(
        SyncConfiguration.Builder(realmUser,
            setOf(
                User::class, Group::class, UserInfo::class, GroupInvite::class, DebtAction::class,
                TransactionRecord::class
            )
        )
        .initialSubscriptions(rerunOnOpen = true) { realm ->
            removeAll()
            add(realm.query<User>("$idSerialName == $0", realmUser.id))
            // Used to get initial user membership, this subscription is deleted afterwards
            add(realm.query<UserInfo>("userId == $0", realmUser.id), "UserInfos")
        }
        .waitForInitialRemoteData()
        .name("syncedRealm")
        .build()
    ).also { createdRealm ->
        realm = createdRealm
        createdRealm.query<UserInfo>().find().toList().let { userInfos ->
            createdRealm.subscriptions.update { realm ->
                remove("UserInfos")
                add(
                    realm.query<GroupInvite>(
                        "inviter == $0 OR invitee == $0",
                        realmUser.id
                    ),
                    "GroupInvites"
                )
                userInfos.forEach { userInfo ->
                    userInfo.groupId!!.let { groupId ->
                        add(realm.query<Group>("$idSerialName == $0", groupId),
                            "${groupId}_Group"
                        )
                        add(realm.query<UserInfo>("groupId == $0", groupId),
                            "${groupId}_UserInfo"
                        )
                        add(realm.query<DebtAction>("groupId == $0", groupId),
                            "${groupId}_DebtAction"
                        )
                    }
                }
            }
            // Wait for subscriptions to sync
            realm.subscriptions.waitForSynchronization()
        }
    }
    return module {
        single { realm }
    }
}

internal fun registerUserObject(newUser: User) {
    realm.writeBlocking {
        copyToRealm(newUser)
    }
}

internal fun Realm.addGroup(groupId: String) {
    runBlocking {
        subscriptions.update { realm ->
            add(realm.query<Group>("$idSerialName == $0", groupId), "${groupId}_Group")
            add(realm.query<UserInfo>("groupId == $0", groupId), "${groupId}_UserInfo")
            add(realm.query<DebtAction>("groupId == $0", groupId),
                "${groupId}_DebtAction")
        }
        realm.subscriptions.waitForSynchronization()
    }
}

internal fun Realm.removeGroup(groupId: String) {
    runBlocking {
        subscriptions.update {
            remove("${groupId}_Group")
            remove("${groupId}_UserInfo")
            remove("${groupId}_DebtAction")
        }
        realm.subscriptions.waitForSynchronization()
    }
}