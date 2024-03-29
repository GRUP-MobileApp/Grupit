package com.grup.service

import com.grup.exceptions.NotCreatedException
import com.grup.dbmanager.DatabaseManager
import com.grup.interfaces.IGroupRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.Group
import com.grup.models.User
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupService(private val dbManager: DatabaseManager) : KoinComponent {
    private val groupRepository: IGroupRepository by inject()
    private val userInfoRepository: IUserInfoRepository by inject()

    suspend fun createGroup(user: User, groupName: String): Group = dbManager.write {
        groupRepository.createGroup(this, user, groupName)?.also { group ->
            userInfoRepository.createUserInfo(this, user, group)
        } ?: throw NotCreatedException("Error creating group $groupName")
    }
}