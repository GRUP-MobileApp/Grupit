package com.grup.interfaces

import com.grup.models.User
import com.grup.models.UserInfo
import kotlinx.coroutines.flow.Flow

internal interface IUserInfoRepository : IRepository {
    suspend fun createUserInfo(user: User, groupId: String): UserInfo?

    fun findUserInfosByGroupId(groupId: String): List<UserInfo>
    fun findMyUserInfosAsFlow(): Flow<List<UserInfo>>
    fun findAllUserInfosAsFlow(): Flow<List<UserInfo>>

    suspend fun updateUserInfo(userInfo: UserInfo, block: (UserInfo) -> Unit): UserInfo?
}
