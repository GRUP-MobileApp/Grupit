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

        // Speculatively reduce debtee's balance
        userInfoRepository.updateUserInfo(this, debtee) {
            userBalance -= settleActionAmount
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

    fun getAllSettleActionsAsFlow() = settleActionRepository.findAllSettleActionsAsFlow()

    suspend fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = dbManager.write {
        if (transactionRecord.status !is TransactionRecord.Status.Pending) {
            throw InvalidTransactionRecordException("Transaction record not pending anymore")
        }

        userInfoRepository.updateUserInfo(this, transactionRecord.userInfo) {
            userBalance += transactionRecord.balanceChange
        }

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
        if (transactionRecord.status !is TransactionRecord.Status.Pending) {
            throw InvalidTransactionRecordException("Transaction record not pending anymore")
        }

        settleActionRepository.updateSettleAction(this, settleAction) {
            findObject(transactionRecord)?.apply {
                status = TransactionRecord.Status.Accepted()
            } ?: throw InvalidTransactionRecordException("Transaction record does not exist")
        }
    }

    suspend fun cancelSettleAction(settleAction: SettleAction) = dbManager.write {
        // SettleAction should only be cancellable if all transactionRecords are Accepted/Rejected
        if (settleAction.pendingAmount > 0 || settleAction.isCompleted) {
            throw InvalidTransactionRecordException("Cannot delete pending or completed settle")
        }

        userInfoRepository.updateUserInfo(this, settleAction.userInfo) {
            userBalance += settleAction.remainingAmount
        }

        // Delete SettleAction if there are no accepted transactions, reduce amount otherwise to
        // preserve completed transactionRecords
        if (settleAction.acceptedAmount == 0.0) {
            settleActionRepository.deleteSettleAction(this, settleAction)
        } else {
            settleActionRepository.updateSettleAction(this, settleAction) {
                amount = acceptedAmount
            }
        }
    }
}
