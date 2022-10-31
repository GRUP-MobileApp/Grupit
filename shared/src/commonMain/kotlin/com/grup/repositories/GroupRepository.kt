package com.grup.repositories

import com.grup.models.Group
import com.grup.interfaces.IGroupRepository
import com.grup.models.UserBalance
import com.grup.objects.idSerialName
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class GroupRepository(
    config: RealmConfiguration = RealmConfiguration.Builder(schema = setOf(Group::class)).build()
) : IGroupRepository {
    private val groupRealm: Realm = Realm.open(config)

    override fun createGroup(group: Group): Group? {
        return groupRealm.writeBlocking {
            copyToRealm(group)
        }
    }

    override fun findGroupById(groupId: String): Group? {
        return groupRealm.query(Group::class, "$idSerialName == $0", groupId).first().find()
    }

    override fun close() {
        groupRealm.close()
    }
}