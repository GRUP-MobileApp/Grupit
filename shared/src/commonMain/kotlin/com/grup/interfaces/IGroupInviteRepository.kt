package com.grup.interfaces

import com.grup.models.GroupInvite
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import kotlinx.coroutines.flow.Flow

internal interface IGroupInviteRepository : IRepository {
    fun createGroupInvite(
        transaction: DatabaseWriteTransaction,
        inviterUserInfo: UserInfo,
        invitee: User
    ): GroupInvite?

    fun findAllGroupInvitesAsFlow(): Flow<List<GroupInvite>>

    fun deleteGroupInvite(transaction: DatabaseWriteTransaction, groupInvite: GroupInvite)
}