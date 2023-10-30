package com.grup.controllers

import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserInfoController : KoinComponent {
    private val userInfoService: UserInfoService by inject()

    fun getMyUserInfosAsFlow() = userInfoService.findMyUserInfosAsFlow()
    fun getAllUserInfosAsFlow() = userInfoService.findAllUserInfosAsFlow()
}