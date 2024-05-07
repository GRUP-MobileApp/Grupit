package com.grup.service

import com.grup.dbmanager.DatabaseManager
import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.InvalidUserBalanceException
import com.grup.exceptions.NotCreatedException
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
        message: String,
        platform: DebtAction.Platform
    ): DebtAction = dbManager.write {
        if (transactionRecords.isEmpty()) {
            throw InvalidTransactionRecordException("Empty transaction records")
        }
        debtActionRepository.createDebtAction(
            this,
            debtee,
            transactionRecords,
            message,
            platform
        ) ?: throw NotCreatedException("Error creating DebtAction for Group with id" +
                " ${debtee.group.id}")
    }

    suspend fun acceptDebtAction(
        debtAction: DebtAction,
        transactionRecord: TransactionRecord,
        allowNegative: Boolean = false
    ) = dbManager.write {
        if (debtAction.platform == DebtAction.Platform.Grupit) {
            val debtorUserInfo: UserInfo = transactionRecord.userInfo
            val debteeUserInfo: UserInfo = debtAction.userInfo
            if (!allowNegative && debtorUserInfo.userBalance - transactionRecord.balanceChange < 0) {
                throw InvalidUserBalanceException(
                    "TransactionRecord between debtor with id " +
                            "${debtorUserInfo.user.id} and debtee with id " +
                            "${debteeUserInfo.user.id} in DebtAction with id " +
                            "${debtAction.id} results in negative balance"
                )
            }

            userInfoRepository.updateUserInfo(this, debtorUserInfo) { userInfo ->
                userInfo.userBalance -= transactionRecord.balanceChange
            }
            userInfoRepository.updateUserInfo(this, debteeUserInfo) { userInfo ->
                userInfo.userBalance += transactionRecord.balanceChange
            }
        }

        debtActionRepository.updateDebtAction(this, debtAction) {
            findObject(transactionRecord)?.apply {
                status = TransactionRecord.Status.Accepted()
            } ?: throw InvalidTransactionRecordException("Transaction record does not exist")
        }
    }

    suspend fun rejectDebtAction(
        debtAction: DebtAction,
        transactionRecord: TransactionRecord
    ) = dbManager.write {
        debtActionRepository.updateDebtAction(this, debtAction) {
            findObject(transactionRecord)?.apply {
                status = TransactionRecord.Status.Rejected
            } ?: throw InvalidTransactionRecordException("Transaction record does not exist")
        }
    }

    fun getAllDebtActionsAsFlow() = debtActionRepository.findAllDebtActionsAsFlow()
}
