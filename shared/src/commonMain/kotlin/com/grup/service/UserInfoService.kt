package com.grup.service

import com.grup.exceptions.InvalidUserBalanceException
import com.grup.dbmanager.DatabaseManager
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.models.UserInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserInfoService(private val dbManager: DatabaseManager) : KoinComponent {
    private val userInfoRepository: IUserInfoRepository by inject()

    suspend fun createUserInfo(user: User, group: Group): UserInfo? = dbManager.write {
        userInfoRepository.createUserInfo(this, user, group)
    }

    fun getMyUserInfosAsFlow() = userInfoRepository.findMyUserInfosAsFlow()
    fun getAllUserInfosAsFlow() = userInfoRepository.findAllUserInfosAsFlow()

    suspend fun applyDebtActionTransactionRecord(
        debtAction: DebtAction,
        transactionRecord: TransactionRecord,
        allowNegative: Boolean = true
    ) = dbManager.write {
        val debtorUserInfo: UserInfo = transactionRecord.userInfo
        val debteeUserInfo: UserInfo = debtAction.userInfo

        if (!allowNegative && debtorUserInfo.userBalance - transactionRecord.balanceChange < 0) {
            throw InvalidUserBalanceException("TransactionRecord between debtor with id " +
                    "${debtorUserInfo.user.id} and debtee with id " +
                    "${debteeUserInfo.user.id} in DebtAction with id " +
                    "${debtAction.id} results in negative balance")
        }

        userInfoRepository.updateUserInfo(this, debtorUserInfo) { userInfo ->
            userInfo.userBalance -= transactionRecord.balanceChange
        }
        userInfoRepository.updateUserInfo(this, debteeUserInfo) { userInfo ->
            userInfo.userBalance += transactionRecord.balanceChange
        }
    }
}