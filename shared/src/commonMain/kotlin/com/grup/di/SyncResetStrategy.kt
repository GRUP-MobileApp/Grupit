package com.grup.di

import io.realm.kotlin.MutableRealm
import io.realm.kotlin.TypedRealm
import io.realm.kotlin.mongodb.exceptions.ClientResetRequiredException
import io.realm.kotlin.mongodb.sync.RecoverUnsyncedChangesStrategy
import io.realm.kotlin.mongodb.sync.SyncSession
import kotlinx.coroutines.runBlocking

internal class SyncResetStrategy(
    private val realmManager: RealmManager
) : RecoverUnsyncedChangesStrategy {
    override fun onAfterReset(before: TypedRealm, after: MutableRealm) {

    }

    override fun onBeforeReset(realm: TypedRealm) {

    }

    override fun onManualResetFallback(
        session: SyncSession,
        exception: ClientResetRequiredException
    ) {
        runBlocking { realmManager.close() }
        exception.executeClientReset()
    }
}