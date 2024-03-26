package com.grup.service

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.InvalidUserBalanceException
import com.grup.exceptions.NotCreatedException
import com.grup.dbmanager.DatabaseManager
import com.grup.interfaces.IDebtActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class DebtActionService(private val dbManager: DatabaseManager) : KoinComponent {
    private val userInfoRepository: IUserInfoRepository by inject()
    private val debtActionRepository: IDebtActionRepository by inject()

    suspend fun createDebtAction(
        debtee: UserInfo,
        transactionRecords: List<TransactionRecord>,
        message: String
    ): DebtAction = dbManager.write {
        if (transactionRecords.isEmpty()) {
            throw InvalidTransactionRecordException("Empty transaction records")
        }
        debtActionRepository.createDebtAction(this, debtee, transactionRecords, message)
            ?: throw NotCreatedException("Error creating DebtAction for Group with id" +
                " ${debtee.group.id}")
    }

    suspend fun acceptDebtAction(
        debtAction: DebtAction,
        transactionRecord: TransactionRecord,
        allowNegative: Boolean = false
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

        debtActionRepository.updateDebtAction(this, debtAction) {
            transactionRecord.status = TransactionRecord.Status.Accepted()
        }
    }

    suspend fun rejectDebtAction(
        debtAction: DebtAction,
        transactionRecord: TransactionRecord
    ) = dbManager.write {
        debtActionRepository.updateDebtAction(this, debtAction) {
            transactionRecord.status = TransactionRecord.Status.Rejected
        }
    }

    fun getAllDebtActionsAsFlow() = debtActionRepository.findAllDebtActionsAsFlow()
}
