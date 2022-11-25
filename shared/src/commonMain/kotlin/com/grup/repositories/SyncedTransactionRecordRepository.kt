package com.grup.repositories

import com.grup.models.TransactionRecord
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.notifications.UpdatedObject
import io.realm.kotlin.ext.query
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.time.Duration

internal class SyncedTransactionRecordRepository(
    private val userManager: LoggedInUserManager
) : TransactionRecordRepository() {
    override val config =
        SyncConfiguration.Builder(userManager.realmUser, setOf(TransactionRecord::class))
        .initialSubscriptions { }.waitForInitialRemoteData()
        .name("transactionRecordRealm")
        .build()

    private val job = CoroutineScope(Dispatchers.Default).launch {
        val userQueryFlow = userManager.userQuery().asFlow()
        userQueryFlow.collect { changes ->
            when(changes) {
                is UpdatedObject -> {
                    if (changes.isFieldChanged("groups")) {
                        realm.subscriptions.update {
                            removeAll()
                            changes.obj.groups.forEach { groupId ->
                                this.add(
                                    realm.query<TransactionRecord>(
                                        "groupId == $0",
                                        groupId),
                                    groupId
                                )
                            }
                        }
                    }
                }
                else -> {}
            }
        }
        if (!realm.subscriptions.waitForSynchronization(Duration.parse("10s"))) {
            TODO("Timeout logic")
        }
    }
}