package com.grup.repositories

import com.grup.exceptions.DoesNotExistException
import com.grup.models.Group
import com.grup.interfaces.IGroupRepository
import com.grup.models.UserInfo
import com.grup.objects.createIdFromString
import com.grup.objects.idSerialName
import io.realm.kotlin.Configuration
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.asFlow

internal open class GroupRepository : BaseRealmRepository(), IGroupRepository {
    override val config: Configuration =
        RealmConfiguration.Builder(schema = setOf(Group::class, UserInfo::class)).build()

    override fun createGroup(group: Group): Group? {
        return realm.writeBlocking {
            copyToRealm(group)
        }
    }

    override fun findGroupById(groupId: String): Group? {
        return realm.query(
            Group::class,
            "$idSerialName == $0",
            createIdFromString(groupId)
        ).first().find()
    }

    override fun updateGroup(group: Group): Group? {
        findGroupById(group.getId())
            ?: throw DoesNotExistException("Group with id ${group.getId()} does not exist")
        return realm.writeBlocking {
            copyToRealm(group, UpdatePolicy.ALL)
        }
    }
}