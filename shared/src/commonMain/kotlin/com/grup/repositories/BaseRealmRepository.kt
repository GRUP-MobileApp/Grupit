package com.grup.repositories

import com.grup.interfaces.IRepository
import io.realm.kotlin.Configuration
import io.realm.kotlin.Realm

internal abstract class BaseRealmRepository : IRepository {
    protected abstract val config: Configuration
    protected val realm: Realm by lazy { Realm.open(config) }

    override fun close() {
        realm.close()
    }
}