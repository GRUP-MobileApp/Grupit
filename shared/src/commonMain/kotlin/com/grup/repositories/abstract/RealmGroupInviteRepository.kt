package com.grup.repositories.abstract

import com.grup.dbmanager.RealmManager
import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import com.grup.interfaces.IGroupInviteRepository
import com.grup.models.GroupInvite
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.models.realm.RealmGroupInvite
import com.grup.models.realm.RealmUser
import com.grup.models.realm.RealmUserInfo
import com.grup.other.getLatest
import com.grup.other.toResolvedListFlow
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow

internal abstract class RealmGroupInviteRepository : IGroupInviteRepository {
    protected abstract val realm: Realm

    override fun createGroupInvite(
        transaction: DatabaseWriteTransaction,
        inviterUserInfo: UserInfo,
        invitee: User
    ): GroupInvite? = with(transaction as RealmManager.RealmWriteTransaction) {
        copyToRealm(
            getLatest(
                RealmGroupInvite(
                    inviterUserInfo = inviterUserInfo as RealmUserInfo,
                    invitee = invitee as RealmUser
                )
            ),
            UpdatePolicy.ERROR
        )
    }

    override fun findAllGroupInvitesAsFlow(): Flow<List<RealmGroupInvite>> {
        return realm.query<RealmGroupInvite>().toResolvedListFlow()
    }

    override fun deleteGroupInvite(
        transaction: DatabaseWriteTransaction,
        groupInvite: GroupInvite
    ) = with(transaction as RealmManager.RealmWriteTransaction) {
        delete(findLatest(groupInvite as RealmGroupInvite)!!)
    }
}