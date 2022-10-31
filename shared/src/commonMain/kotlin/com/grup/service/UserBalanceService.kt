package com.grup.service

import com.grup.interfaces.IUserBalanceRepository
import com.grup.models.UserBalance
import com.grup.objects.Id
import com.grup.objects.createIdFromString
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserBalanceService : KoinComponent {
    private val userBalanceRepository: IUserBalanceRepository by inject()

    fun createUserBalance(userBalance: UserBalance): UserBalance? {
        return userBalanceRepository.createUserBalance(userBalance)
    }

    fun createZeroUserBalance(groupId: String, userId: String): UserBalance {
        return UserBalance().apply {
            this.groupId = createIdFromString(groupId)
            this.userId = createIdFromString(userId)
            this.balance = 0.0
        }
    }

    fun getUserBalancesByGroupId(groupId: String): List<UserBalance> {
        return userBalanceRepository.findUserBalancesByGroupId(groupId)
    }

    fun getUserIdsByGroupId(groupId: String): List<Id> {
        return getUserBalancesByGroupId(groupId).map { userBalance -> userBalance.userId!! }
    }

    fun userBalanceExists(groupId: String, userId: String): Boolean {
        return userBalanceRepository.findUserBalanceByUserAndGroupId(userId, groupId) != null
    }

    fun updateUserBalance(groupId: String, userId: String, balanceChange: Double): UserBalance? {
        TODO("Not yet implemented")
    }
}