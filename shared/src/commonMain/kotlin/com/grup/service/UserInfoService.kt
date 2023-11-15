package com.grup.service

import com.grup.exceptions.NegativeBalanceException
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserInfoService : KoinComponent {
    private val userInfoRepository: IUserInfoRepository by inject()

    suspend fun createUserInfo(user: User, group: Group): UserInfo? {
        return userInfoRepository.createUserInfo(user, group)
    }

    fun findUserInfosByGroupId(groupId: String): List<UserInfo> {
        return userInfoRepository.findUserInfosByGroupId(groupId)
    }

    fun findMyUserInfosAsFlow() = userInfoRepository.findMyUserInfosAsFlow()
    fun findAllUserInfosAsFlow() = userInfoRepository.findAllUserInfosAsFlow()

    suspend fun applyDebtActionTransactionRecord(
        debtAction: DebtAction,
        transactionRecord: TransactionRecord,
        allowNegative: Boolean = true
    ) {
        val debtorUserInfo: UserInfo = transactionRecord.userInfo
        val debteeUserInfo: UserInfo = debtAction.userInfo

        if (!allowNegative && debtorUserInfo.userBalance - transactionRecord.balanceChange < 0) {
            throw NegativeBalanceException("TransactionRecord between debtor with id " +
                    "${debtorUserInfo.user.id} and debtee with id " +
                    "${debteeUserInfo.user.id} in DebtAction with id " +
                    "${debtAction.id} results in negative balance")
        }

        userInfoRepository.updateUserInfo(debtorUserInfo) { userInfo ->
            userInfo.userBalance -= transactionRecord.balanceChange
        }
        userInfoRepository.updateUserInfo(debteeUserInfo) { userInfo ->
            userInfo.userBalance += transactionRecord.balanceChange
        }
    }

    suspend fun applySettleActionTransactionRecord(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) {
        val debteeUserInfo: UserInfo = transactionRecord.userInfo
        val debtorUserInfo: UserInfo = settleAction.userInfo

        if (debtorUserInfo.userBalance + transactionRecord.balanceChange > 0) {
            throw NegativeBalanceException("Repayment from " +
                    "${debtorUserInfo.user.displayName} to " +
                    "${debteeUserInfo.user.displayName} results in overpayment")
        }

        userInfoRepository.updateUserInfo(debteeUserInfo) { userInfo ->
            userInfo.userBalance -= transactionRecord.balanceChange
        }
        userInfoRepository.updateUserInfo(debtorUserInfo) { userInfo ->
            userInfo.userBalance += transactionRecord.balanceChange
        }
    }
}