package com.grup.repositories.abstract

import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import com.grup.dbmanager.RealmManager
import com.grup.interfaces.IGroupRepository
import com.grup.models.Group
import com.grup.models.User
import com.grup.models.realm.RealmGroup
import com.grup.other.idSerialName
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query

internal abstract class RealmGroupRepository : IGroupRepository {
    protected abstract val realm: Realm

    override fun createGroup(
        transaction: DatabaseWriteTransaction,
        user: User,
        groupName: String
    ): RealmGroup? = with(transaction as RealmManager.RealmWriteTransaction) {
        copyToRealm(
            RealmGroup().apply {
                this.groupName = groupName
            },
            UpdatePolicy.ERROR
        )
    }

    override fun findGroupById(groupId: String): RealmGroup? {
        return realm.query<RealmGroup>("$idSerialName == $0", groupId).first().find()
    }

    override fun updateGroup(
        transaction: DatabaseWriteTransaction,
        group: Group,
        block: Group.() -> Unit
    ): RealmGroup? = with(transaction as RealmManager.RealmWriteTransaction) {
        findLatest(group as RealmGroup)!!.apply(block)
    }
}
