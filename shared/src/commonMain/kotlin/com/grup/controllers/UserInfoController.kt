package com.grup.controllers

import com.grup.models.Group
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserInfoController : KoinComponent {
    private val userInfoService: UserInfoService by inject()

    fun getUserInfosByGroupIdAsFlow(group: Group) =
        userInfoService.findUserInfosByGroupIdAsFlow(group.getId())
}