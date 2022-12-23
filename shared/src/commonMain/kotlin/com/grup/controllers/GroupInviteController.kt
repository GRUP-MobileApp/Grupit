package com.grup.controllers

import com.grup.models.GroupInvite
import com.grup.service.GroupInviteService
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object GroupInviteController : KoinComponent {
    private val groupInviteService: GroupInviteService by inject()

    fun getAllGroupInvitesAsFlow(): Flow<List<GroupInvite>> {
        return groupInviteService.getAllGroupInvitesAsFlow()
    }
}