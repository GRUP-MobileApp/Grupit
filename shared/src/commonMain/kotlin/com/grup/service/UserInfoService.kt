package com.grup.service

import com.grup.exceptions.NegativeBalanceException
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.*
import com.grup.other.getCurrentTime
import kotlinx.coroutines.flow.first
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserInfoService : KoinComponent {
    private val userInfoRepository: IUserInfoRepository by inject()

    suspend fun createUserInfo(user: User, groupId: String): UserInfo? {
        return userInfoRepository.createUserInfo(user, groupId)
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
        val debtorUserInfo: UserInfo = transactionRecord.debtorUserInfo
        val debteeUserInfo: UserInfo = debtAction.debteeUserInfo

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

    suspend fun applySettleAction(settleAction: SettleAction) {
        val debteeUserInfo: UserInfo = settleAction.debteeUserInfo

        if (debteeUserInfo.userBalance < settleAction.settleAmount) {
            throw NegativeBalanceException("SettleAction with id ${settleAction.id}" +
                    "results in negative balance")
        }

        userInfoRepository.updateUserInfo(debteeUserInfo) { userInfo ->
            userInfo.userBalance -= settleAction.settleAmount
        }
    }

    suspend fun applyPartialSettleActionTransactionRecord(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) {
        val debtorUserInfo: UserInfo = transactionRecord.debtorUserInfo

        if (transactionRecord.balanceChange > settleAction.remainingAmount) {
            throw NegativeBalanceException("TransactionRecord between debtor with id " +
                    "${debtorUserInfo.user.id} and debtee with id " +
                    "${settleAction.debteeUserInfo} in SettleAction with id " +
                    "${settleAction.id} results in negative remaining amount")
        }

        userInfoRepository.updateUserInfo(debtorUserInfo) { userInfo ->
            userInfo.userBalance += transactionRecord.balanceChange
        }
    }

    suspend fun updateLatestTime(group: Group) {
        val myUserInfo: UserInfo =
            findMyUserInfosAsFlow().first().find {
                it.groupId == group.id
            }!!

        userInfoRepository.updateUserInfo(myUserInfo) { userInfo ->
            userInfo.latestViewDate = getCurrentTime()
        }
    }
}