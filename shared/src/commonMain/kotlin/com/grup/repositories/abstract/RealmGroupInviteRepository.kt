package com.grup.repositories.abstract

import com.grup.interfaces.IGroupInviteRepository
import com.grup.models.GroupInvite
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal abstract class RealmGroupInviteRepository : IGroupInviteRepository {
    protected abstract val realm: Realm

    override fun createGroupInvite(groupInvite: GroupInvite): GroupInvite? {
        return realm.writeBlocking {
            copyToRealm(groupInvite)
        }
    }

    override fun findAllGroupInvitesAsFlow(): Flow<List<GroupInvite>> {
        return realm.query<GroupInvite>().asFlow().map { it.list }
    }

    override fun updateGroupInviteStatus(groupInvite: GroupInvite,
                                         status: GroupInvite.RequestStatus): GroupInvite {
        return realm.writeBlocking {
            findLatest(groupInvite)!!.apply {
                groupInvite.status = status
            }
        }
    }

    override fun close() {
        realm.close()
    }
}