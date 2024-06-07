package com.grup.service

import com.grup.dbmanager.DatabaseManager
import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
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

        // Accept user's own transaction if included
        transactionRecords.find { it.userInfo.user.id == debtee.user.id }?.apply {
            if (transactionRecords.size == 1) {
                throw InvalidTransactionRecordException(
                    "Can't create a debt request with only yourself"
                )
            }
            status = TransactionRecord.Status.Accepted()
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
        transactionRecord: TransactionRecord
    ) = dbManager.write {
        if (transactionRecord.status !is TransactionRecord.Status.Pending) {
            throw InvalidTransactionRecordException("Transaction record not pending anymore")
        }

        // Only update balances if DebtAction is for Grupit
        if (debtAction.platform == DebtAction.Platform.Grupit) {
            val debtorUserInfo: UserInfo = transactionRecord.userInfo
            val debteeUserInfo: UserInfo = debtAction.userInfo

            userInfoRepository.updateUserInfo(this, debtorUserInfo) {
                userBalance -= transactionRecord.balanceChange
            }
            userInfoRepository.updateUserInfo(this, debteeUserInfo) {
                userBalance += transactionRecord.balanceChange
            }
        }

        debtActionRepository.updateDebtAction(this, debtAction) {
            findObject(transactionRecord)?.apply {
                status = TransactionRecord.Status.Accepted()
            } ?: throw InvalidTransactionRecordException("Transaction record does not exist")
        } ?: throw NotFoundException("Debt Action doesn't exist")
    }

    suspend fun rejectDebtAction(
        debtAction: DebtAction,
        transactionRecord: TransactionRecord
    ) = dbManager.write {
        if (transactionRecord.status !is TransactionRecord.Status.Pending) {
            throw InvalidTransactionRecordException("Transaction record not pending anymore")
        }

        debtActionRepository.updateDebtAction(this, debtAction) {
            findObject(transactionRecord)?.apply {
                status = TransactionRecord.Status.Rejected()
            } ?: throw InvalidTransactionRecordException("Transaction record does not exist")
        } ?: throw NotFoundException("Debt Action doesn't exist")
    }

    fun getAllDebtActionsAsFlow() = debtActionRepository.findAllDebtActionsAsFlow()
}
