package com.grup.service

import com.grup.dbmanager.DatabaseManager
import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.InvalidUserBalanceException
import com.grup.exceptions.NotCreatedException
import com.grup.interfaces.ISettleActionRepository
import com.grup.interfaces.IUserInfoRepository
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.UserInfo
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SettleActionService(private val dbManager: DatabaseManager) : KoinComponent {
    private val userInfoRepository: IUserInfoRepository by inject()
    private val settleActionRepository: ISettleActionRepository by inject()

    suspend fun createSettleAction(
        debtee: UserInfo,
        settleActionAmount: Double
    ): SettleAction = dbManager.write {
        if (debtee.userBalance - settleActionAmount < 0) {
            throw InvalidUserBalanceException("SettleAction results in negative balance")
        }
        userInfoRepository.updateUserInfo(this, debtee) { userInfo ->
            userInfo.userBalance -= settleActionAmount
        }
        settleActionRepository.createSettleAction(this, debtee, settleActionAmount)
            ?: throw NotCreatedException("Error creating SettleAction for user with id" +
                    " ${debtee.user.id}")
    }

    suspend fun createSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) {
        if (settleAction.remainingAmount < transactionRecord.balanceChange) {
            throw InvalidTransactionRecordException("Can't settle for more than remaining amount")
        }
        if (transactionRecord.userInfo.userBalance + transactionRecord.balanceChange > 0) {
            throw InvalidTransactionRecordException("Settle Transaction results in overpayment")
        }
        return dbManager.write {
            settleActionRepository.updateSettleAction(this, settleAction) {
                transactionRecords.add(
                    TransactionRecord.Companion.DataTransactionRecord(
                        findObject(transactionRecord.userInfo)!!, transactionRecord.balanceChange
                    )
                )
            }
        }
    }

    suspend fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = dbManager.write {
        settleActionRepository.updateSettleAction(this, settleAction) {
            findObject(transactionRecord)?.apply {
                status = TransactionRecord.Status.Accepted()
            } ?: throw InvalidTransactionRecordException("Transaction record does not exist")
        }
    }
    suspend fun rejectSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = dbManager.write {
        settleActionRepository.updateSettleAction(this, settleAction) {
            findObject(transactionRecord)?.apply {
                status = TransactionRecord.Status.Accepted()
            } ?: throw InvalidTransactionRecordException("Transaction record does not exist")
        }
    }

    fun getAllSettleActionsAsFlow() = settleActionRepository.findAllSettleActionsAsFlow()
}
