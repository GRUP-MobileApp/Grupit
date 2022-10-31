package com.grup.controllers

import com.grup.exceptions.EmptyBalancesException
import com.grup.exceptions.EntityAlreadyExistsException
import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.models.UserBalance
import com.grup.objects.Id
import com.grup.objects.throwIf
import com.grup.service.GroupService
import com.grup.service.UserBalanceService
import com.grup.service.UserService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class UserBalanceController : KoinComponent {
    private val userBalanceService: UserBalanceService by inject()
    private val userService: UserService by inject()
    private val groupService: GroupService by inject()

    fun createUserBalance(groupId: String, userId: String): UserBalance {
        throwIf(!groupService.groupIdExists(groupId)) {
            NotFoundException("Group with id $groupId doesn't exist")
        }
        throwIf(!userService.userIdExists(userId)) {
            NotFoundException("User with id $userId doesn't exist")
        }
        throwIf(userBalanceService.userBalanceExists(groupId, userId)) {
            EntityAlreadyExistsException("UserBalance for user with id $userId in group with " +
                    "id $groupId already exists")
        }

        val userBalance: UserBalance = userBalanceService.createZeroUserBalance(groupId, userId)

        return userBalanceService.createUserBalance(userBalance)
            ?: throw NotCreatedException("Error creating UserBalance for User with id $userId " +
                    "in Group with id $groupId")
    }

    fun getUserBalancesByGroupId(groupId: String): List<UserBalance> {
        val userBalances: List<UserBalance> = userBalanceService.getUserBalancesByGroupId(groupId)
        throwIf(userBalances.isEmpty()) {
            EmptyBalancesException("No UserBalances found for Group with id $groupId")
        }
        return userBalances
    }

    fun getUsersByGroupId(groupId: String): List<Id> {
        val userIds: List<Id> = userBalanceService.getUserIdsByGroupId(groupId)
        throwIf(userIds.isEmpty()) {
            EmptyBalancesException("No UserBalances found for Group with id $groupId")
        }
        return userIds
    }
}