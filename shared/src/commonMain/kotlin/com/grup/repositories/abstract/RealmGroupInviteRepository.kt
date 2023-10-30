package com.grup.repositories.abstract

import com.grup.interfaces.IGroupInviteRepository
import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.User
import com.grup.models.realm.RealmGroupInvite
import com.grup.models.realm.RealmUser
import com.grup.other.copyNestedObjectToRealm
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

internal abstract class RealmGroupInviteRepository : IGroupInviteRepository {
    protected abstract val realm: Realm

    override fun createGroupInvite(inviter: User, invitee: User, group: Group): RealmGroupInvite? {
        return realm.writeBlocking {
            copyNestedObjectToRealm(
                RealmGroupInvite().apply {
                    this._inviter = inviter as RealmUser
                    this._inviterId = inviter.id
                    this._inviteeId = invitee.id
                    this._groupId = group.id
                    this._groupName = group.groupName
                }
            )
        }
    }

    override fun findAllGroupInvitesAsFlow(): Flow<List<RealmGroupInvite>> {
        return realm.query<RealmGroupInvite>().toResolvedListFlow()
    }

    override suspend fun deleteGroupInvite(groupInvite: GroupInvite) {
        return realm.write {
            delete(findLatest(groupInvite as RealmGroupInvite)!!)
        }
    }
}