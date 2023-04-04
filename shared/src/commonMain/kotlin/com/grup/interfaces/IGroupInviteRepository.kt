package com.grup.interfaces

import com.grup.models.GroupInvite
import kotlinx.coroutines.flow.Flow

internal interface IGroupInviteRepository : IRepository {
    fun createGroupInvite(groupInvite: GroupInvite): GroupInvite?

    fun findAllGroupInvitesAsFlow(): Flow<List<GroupInvite>>

    suspend fun deleteGroupInvite(groupInvite: GroupInvite)
}