package com.grup.interfaces

import com.grup.models.UserBalance
import java.math.BigDecimal

interface IUserBalanceRepository {
    fun createUserBalance(userBalance: UserBalance): UserBalance?

    fun findUserBalanceByUserAndGroupId(userId: String, groupId: String): UserBalance?
    fun findUserBalancesByGroupId(groupId: String): List<UserBalance>

    fun updateUserBalance(newBalance: BigDecimal): UserBalance?
}