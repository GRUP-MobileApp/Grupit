package com.grup.controllers

import com.grup.models.Group
import com.grup.models.UserInfo
import com.grup.service.GroupService
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserInfoController : KoinComponent {
    private val groupService: GroupService by inject()
    private val userInfoService: UserInfoService by inject()

    fun getUserInfoByGroupId(group: Group): List<UserInfo> {
        return userInfoService.findUserInfosByGroup(group._id)
    }
}