package com.grup.service

import com.grup.exceptions.NegativeBalanceException
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.*
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

    fun applyDebtActionTransactionRecord(debtAction: DebtAction,
                                         transactionRecord: TransactionRecord,
                                         allowNegative: Boolean = true) {
        val debtorUserInfo: UserInfo = transactionRecord.debtorUserInfo!!
        val debteeUserInfo: UserInfo = debtAction.debteeUserInfo!!

        if (allowNegative && debtorUserInfo.userBalance - transactionRecord.balanceChange!! < 0) {
            throw NegativeBalanceException("TransactionRecord between debtor with id " +
                    "${debtorUserInfo.userId} and debtee with id ${debteeUserInfo.userId} " +
                    "in DebtAction with id ${debtAction.getId()} results in negative balance")
        }

        userInfoRepository.updateUserInfo(debtorUserInfo) { userInfo ->
            userInfo.userBalance -= transactionRecord.balanceChange!!
        }
        userInfoRepository.updateUserInfo(debteeUserInfo) { userInfo ->
            userInfo.userBalance += transactionRecord.balanceChange!!
        }
    }

    fun applySettleAction(settleAction: SettleAction) {
        val debteeUserInfo: UserInfo = settleAction.debteeUserInfo!!

        if (debteeUserInfo.userBalance - settleAction.settleAmount!! < 0) {
            throw NegativeBalanceException("SettleAction with id ${settleAction.getId()}" +
                    "results in negative balance")
        }

        userInfoRepository.updateUserInfo(debteeUserInfo) { userInfo ->
            userInfo.userBalance -= settleAction.settleAmount!!
        }
    }

    fun applyPartialSettleActionTransactionRecord(settleAction: SettleAction,
                                                  transactionRecord: TransactionRecord) {
        val debtorUserInfo: UserInfo = transactionRecord.debtorUserInfo!!

        if (transactionRecord.balanceChange!! > settleAction.remainingAmount) {
            throw NegativeBalanceException("TransactionRecord between debtor with id " +
                    "${debtorUserInfo.userId} and debtee with id ${settleAction.debteeUserInfo!!} in " +
                    "SettleAction with id ${settleAction.getId()} results in negative " +
                    "remaining amount")
        }

        userInfoRepository.updateUserInfo(debtorUserInfo) { userInfo ->
            userInfo.userBalance += transactionRecord.balanceChange!!
        }
    }
}