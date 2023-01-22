package com.grup.controllers

import com.grup.models.User
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object UserInfoController : KoinComponent {
    private val userInfoService: UserInfoService by inject()

    fun getMyUserInfosAsFlow(user: User) = userInfoService.findMyUserInfosAsFlow(user)
    fun getAllUserInfosAsFlow() = userInfoService.findAllUserInfosAsFlow()
}