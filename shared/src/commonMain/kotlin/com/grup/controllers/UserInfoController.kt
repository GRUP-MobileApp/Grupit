package com.grup.controllers

import com.grup.models.Group
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserInfoController : KoinComponent {
    private val userInfoService: UserInfoService by inject()

    fun getMyUserInfosAsFlow() = userInfoService.findMyUserInfosAsFlow()
    fun getAllUserInfosAsFlow() = userInfoService.findAllUserInfosAsFlow()

    suspend fun updateLatestTime(group: Group) =
        userInfoService.updateLatestTime(group)
}