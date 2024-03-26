package com.grup.interfaces

import com.grup.models.Group
import com.grup.models.User
import com.grup.models.UserInfo
import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import kotlinx.coroutines.flow.Flow

internal interface IUserInfoRepository : IRepository {
    fun createUserInfo(
        transaction: DatabaseWriteTransaction,
        user: User,
        group: Group
    ): UserInfo?

    fun findMyUserInfosAsFlow(): Flow<List<UserInfo>>
    fun findAllUserInfosAsFlow(): Flow<List<UserInfo>>

    fun updateUserInfo(
        transaction: DatabaseWriteTransaction,
        userInfo: UserInfo,
        block: (UserInfo) -> Unit
    ): UserInfo?
}
