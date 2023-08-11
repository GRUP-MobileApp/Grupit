package com.grup.interfaces

import com.grup.models.Group
import com.grup.models.GroupInvite
import com.grup.models.User
import kotlinx.coroutines.flow.Flow

internal interface IGroupInviteRepository : IRepository {
    fun createGroupInvite(inviter: User, invitee: User, group: Group): GroupInvite?

    fun findAllGroupInvitesAsFlow(): Flow<List<GroupInvite>>

    suspend fun deleteGroupInvite(groupInvite: GroupInvite)
}