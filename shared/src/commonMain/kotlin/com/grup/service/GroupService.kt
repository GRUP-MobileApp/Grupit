package com.grup.service

import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.Group
import com.grup.models.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupService : KoinComponent {
    private val groupRepository: IGroupRepository by inject()
    private val userInfoRepository: IUserInfoRepository by inject()

    suspend fun createGroup(user: User, groupName: String): Group {
        return groupRepository.createGroup(user, groupName)?.also { group ->
            userInfoRepository.createUserInfo(user, group)
        } ?: throw NotCreatedException("Error creating group $groupName")
    }

    fun getAllGroupsAsFlow() = groupRepository.findAllGroupsAsFlow()
}