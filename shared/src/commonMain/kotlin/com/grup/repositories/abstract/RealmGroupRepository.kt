package com.grup.repositories.abstract

import com.grup.models.Group
import com.grup.interfaces.IGroupRepository
import com.grup.other.idSerialName
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal abstract class RealmGroupRepository : IGroupRepository {
    protected abstract val realm: Realm

    override suspend fun createGroup(group: Group): Group? {
        realm.syncSession.downloadAllServerChanges()
        return realm.write {
            copyToRealm(group)
        }
    }

    override fun findGroupById(groupId: String): Group? {
        return realm.query<Group>("$idSerialName == $0", groupId).first().find()
    }

    override fun findAllGroupsAsFlow(): Flow<List<Group>> {
        return realm.query<Group>().find().asFlow().map { it.list }
    }

    override suspend fun updateGroup(group: Group, block: Group.() -> Unit): Group? {
        return realm.write {
            findLatest(group)!!.apply(block)
        }
    }
}
