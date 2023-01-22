package com.grup.service

import com.grup.exceptions.NegativeBalanceException
import com.grup.exceptions.NotFoundException
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserInfoService : KoinComponent {
    private val userInfoRepository: IUserInfoRepository by inject()

    fun createUserInfo(user: User, groupId: String): UserInfo? {
        return userInfoRepository.createUserInfo(
            UserInfo().apply {
                this.userId = user.getId()
                this.groupId = groupId
                this.nickname = user.username
            }
        )
    }

    fun findMyUserInfosAsFlow(user: User) = userInfoRepository.findMyUserInfosAsFlow(user.getId())
    fun findAllUserInfosAsFlow() = userInfoRepository.findAllUserInfosAsFlow()

    private fun findUserInfoByUserId(userId: String, groupId: String): UserInfo? {
        return userInfoRepository.findUserInfoByUser(userId, groupId)
    }

//    fun applyTransactionRecord(transactionRecord: TransactionRecord,
//                               allowNegative: Boolean = true) {
//        val debtorUserInfo: UserInfo =
//            findUserInfoByUserId(transactionRecord.debtor!!, transactionRecord.groupId!!)
//                ?: throw NotFoundException("User with id ${transactionRecord.debtor!!} not found " +
//                        "in Group with id ${transactionRecord.groupId}")
//        val debteeUserInfo: UserInfo =
//            findUserInfoByUserId(transactionRecord.debtee!!, transactionRecord.groupId!!)
//                ?: throw NotFoundException("User with id ${transactionRecord.debtee!!} not found " +
//                        "in Group with id ${transactionRecord.groupId}")
//
//        if (allowNegative && debtorUserInfo.userBalance - transactionRecord.balanceChange!! < 0) {
//            throw NegativeBalanceException("Transaction with id ${transactionRecord.getId()}" +
//                    "results in negative balance")
//        }
//
//        userInfoRepository.updateUserInfo(debtorUserInfo) {
//            it.userBalance -= transactionRecord.balanceChange!!
//        }
//        userInfoRepository.updateUserInfo(debteeUserInfo) {
//            it.userBalance += transactionRecord.balanceChange!!
//        }
//    }
}