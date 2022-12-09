package com.grup.di

import com.grup.models.Group
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.other.Id
import com.grup.other.RealmUser
import com.grup.other.asString
import com.grup.other.idSerialName
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.subscriptions
import kotlinx.coroutines.runBlocking
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import org.koin.core.module.Module
import org.koin.dsl.module

internal lateinit var realm: Realm

internal fun createSyncedRealmModule(realmUser: RealmUser): Module {
    realm = Realm.open(SyncConfiguration.Builder(realmUser,
        setOf(User::class, Group::class, UserInfo::class, TransactionRecord::class))
        .initialSubscriptions(rerunOnOpen = true) { realm ->
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
            runBlocking {
                createdRealm.subscriptions.update { realm ->
                    remove("UserInfos")
                    userInfos.forEach { userInfo ->
                        userInfo.groupId!!.let { groupId ->
                            add(
                                realm.query<Group>("$idSerialName == $0", groupId),
                                "${groupId.asString()}_Group"
                            )
                            add(
                                realm.query<UserInfo>("groupId == $0", groupId),
                                "${groupId.asString()}_UserInfo"
                            )
                            add(
                                realm.query<TransactionRecord>("groupId == $0", groupId),
                                "${groupId.asString()}_TransactionRecord"
                            )
                        }
                    }
                }
            }
        }
    }
    // Wait for subscriptions to sync
    runBlocking {
        realm.subscriptions.waitForSynchronization()
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

internal fun Realm.addGroup(groupId: Id) {
    runBlocking {
        subscriptions.update { realm ->
            add(realm.query<Group>("$idSerialName == $0", groupId),
                "${groupId.asString()}_Group")
            add(realm.query<UserInfo>("groupId == $0", groupId),
                "${groupId.asString()}_UserInfo")
            add(realm.query<TransactionRecord>("groupId == $0", groupId),
                "${groupId.asString()}_TransactionRecord")
        }
    }
}

internal fun Realm.removeGroup(groupId: Id) {
    runBlocking {
        subscriptions.update {
            remove("${groupId.asString()}_Group")
            remove("${groupId.asString()}_UserInfo")
            remove("${groupId.asString()}_TransactionRecord")
        }
    }
}