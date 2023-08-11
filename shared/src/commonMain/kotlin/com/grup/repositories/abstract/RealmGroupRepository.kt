package com.grup.repositories.abstract

import com.grup.interfaces.IGroupRepository
import com.grup.models.Group
import com.grup.models.User
import com.grup.models.realm.RealmGroup
import com.grup.other.copyNestedObjectToRealm
import com.grup.other.idSerialName
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import io.realm.kotlin.mongodb.subscriptions
import kotlinx.coroutines.flow.Flow

internal abstract class RealmGroupRepository : IGroupRepository {
    protected abstract val realm: Realm

    override suspend fun createGroup(user: User, groupName: String): RealmGroup? {
        val group: RealmGroup = RealmGroup().apply {
            _groupName = groupName
        }
        realm.subscriptions.update {
            add(realm.query<RealmGroup>("$idSerialName == $0", group.id),
                "${group.id}_Group")
        }
        return realm.write {
            copyNestedObjectToRealm(group)
        }
    }

    override fun findGroupById(groupId: String): RealmGroup? {
        return realm.query<RealmGroup>("$idSerialName == $0", groupId).first().find()
    }

    override fun findAllGroupsAsFlow(): Flow<List<RealmGroup>> {
        return realm.query<RealmGroup>().toResolvedListFlow()
    }

    override suspend fun updateGroup(group: Group, block: Group.() -> Unit): RealmGroup? {
        return realm.write {
            findLatest(group as RealmGroup)!!.apply(block)
        }
    }
}
