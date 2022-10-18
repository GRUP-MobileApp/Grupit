package com.grup.repositories

import com.grup.models.Group
import com.grup.interfaces.IGroupRepository
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

class TestGroupRepository : IGroupRepository {
    private val config = RealmConfiguration.Builder(schema = setOf(Group::class)).build()
    private val groupRealm: Realm = Realm.open(config)

    override fun createGroup(group: Group): Group? {
        return groupRealm.writeBlocking {
            copyToRealm(group)
        }
    }

    override fun findGroupById(groupId: String): Group? {
        return groupRealm.query(Group::class, "id == $0", groupId).first().find()
    }
}