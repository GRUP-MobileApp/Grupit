package com.grup.repositories

import com.grup.models.UserInfo
import com.grup.repositories.abstract.RealmUserInfoRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class TestUserInfoRepository : RealmUserInfoRepository() {
    private val config = RealmConfiguration.Builder(schema = setOf(UserInfo::class)).build()
    override val realm: Realm by lazy { Realm.open(config) }
}
