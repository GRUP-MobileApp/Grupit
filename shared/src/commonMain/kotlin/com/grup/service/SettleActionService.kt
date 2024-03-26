package com.grup.service

import com.grup.exceptions.InvalidUserBalanceException
import com.grup.exceptions.NotCreatedException
import com.grup.dbmanager.DatabaseManager
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
    ) = dbManager.write {
        settleActionRepository.updateSettleAction(this, settleAction) {
            transactionRecords.add(
                TransactionRecord.Companion.DataTransactionRecord(
                    findObject(transactionRecord.userInfo)!!, transactionRecord.balanceChange
                )
            )
        }
    }

    suspend fun acceptSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = dbManager.write {
        settleActionRepository.updateSettleAction(this, settleAction) {
            transactionRecord.status = TransactionRecord.Status.Accepted()
        }
    }
    suspend fun rejectSettleActionTransaction(
        settleAction: SettleAction,
        transactionRecord: TransactionRecord
    ) = dbManager.write {
        settleActionRepository.updateSettleAction(this, settleAction) {
            transactionRecords[transactionRecords.indexOf(transactionRecord)].status =
                TransactionRecord.Status.Rejected
        }
    }

    fun getAllSettleActionsAsFlow() = settleActionRepository.findAllSettleActionsAsFlow()
}
