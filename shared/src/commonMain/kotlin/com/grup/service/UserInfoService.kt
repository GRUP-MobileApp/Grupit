package com.grup.service

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.NotFoundException
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import com.grup.other.Id
import com.grup.other.asString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserInfoService : KoinComponent {
    private val userInfoRepository: IUserInfoRepository by inject()

    fun createUserInfo(userInfo: UserInfo): UserInfo? {
        return userInfoRepository.createUserInfo(userInfo)
    }

    fun findUserInfoByUser(userId: Id, groupId: Id): UserInfo? {
        return userInfoRepository.findUserInfoByUser(userId, groupId)
    }

    fun findUserInfosByGroup(groupId: Id): List<UserInfo> {
        return userInfoRepository.findUserInfosByGroup(groupId)
    }

    fun applyTransactionRecord(transactionRecord: TransactionRecord) {
        if (transactionRecord.balanceChanges.isEmpty()) {
            throw InvalidTransactionRecordException("Empty transaction record")
        }

        val userInfos: MutableList<UserInfo> = mutableListOf()
        transactionRecord.balanceChanges.forEach { balanceChangeRecord ->
            // Gets corresponding UserInfo from list
            val userInfo = userInfos.find { foundUserInfo ->
                foundUserInfo.userId == balanceChangeRecord.userId
            } ?: run {
                // Add to list if not found
                findUserInfoByUser(balanceChangeRecord.userId, transactionRecord.groupId!!)?.also {
                    userInfos.add(it)
                } ?: throw NotFoundException("User with id " +
                        "${balanceChangeRecord.userId.asString()} not found in Group with " +
                        "id ${transactionRecord.groupId.asString()}")
            }
            // Apply balance change
            userInfo.apply {
                this.userBalance += balanceChangeRecord.balanceChange
            }
        }
        userInfos.forEach { updatedUserInfo ->
            userInfoRepository.updateUserInfo(updatedUserInfo)
        }
    }
}