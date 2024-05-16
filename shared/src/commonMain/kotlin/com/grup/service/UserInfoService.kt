package com.grup.service

import com.grup.dbmanager.DatabaseManager
import com.grup.interfaces.IUserInfoRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserInfoService(private val dbManager: DatabaseManager) : KoinComponent {
    private val userInfoRepository: IUserInfoRepository by inject()

    fun getMyUserInfosAsFlow() = userInfoRepository.findMyUserInfosAsFlow()
    fun getAllUserInfosAsFlow() = userInfoRepository.findAllUserInfosAsFlow()
}