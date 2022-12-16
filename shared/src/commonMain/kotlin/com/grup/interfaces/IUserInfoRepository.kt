package com.grup.interfaces

import com.grup.models.UserInfo
import com.grup.other.Id

internal interface IUserInfoRepository : IRepository {
    fun createUserInfo(userInfo: UserInfo): UserInfo?

    fun findUserInfoByUser(userId: Id, groupId: Id): UserInfo?
    fun findUserInfosByGroup(groupId: Id): List<UserInfo>

    fun updateUserInfo(userInfo: UserInfo): UserInfo?
}
