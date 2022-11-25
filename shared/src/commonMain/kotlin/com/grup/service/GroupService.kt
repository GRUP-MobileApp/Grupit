package com.grup.service

import com.grup.exceptions.DoesNotExistException
import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.UserAlreadyInGroupException
import com.grup.models.Group
import com.grup.interfaces.IGroupRepository
import com.grup.models.TransactionRecord
import com.grup.models.User
import com.grup.objects.throwIf
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class GroupService : KoinComponent {
    private val groupRepository: IGroupRepository by inject()

    fun createGroup(group: Group): Group? {
        return groupRepository.createGroup(group)
    }

    fun getByGroupId(groupId: String): Group? {
        return groupRepository.findGroupById(groupId)
    }

    fun addUserToGroup(user: User, group: Group) {
        throwIf(group.userInfo.find { it.userId == user.getId() } != null) {
            UserAlreadyInGroupException("User with id ${user.getId()} is already in " +
                    "Group with id ${group.getId()}")
        }
        group.userInfo.add(
            Group.UserInfo().apply {
                this.userId = user.getId()
                this.username = user.username
                this.userBalance = 0.0
            }
        )
        groupRepository.updateGroup(group)
    }

    fun applyTransactionRecord(transactionRecord: TransactionRecord) {
        val groupId = transactionRecord.groupId.toString()
        val group: Group = getByGroupId(groupId)
            ?: throw DoesNotExistException("Error posting transaction, " +
                    "Group with id $groupId doesn't exist")

        try {
            transactionRecord.balanceChanges.forEach { balanceChangeRecord ->
                group.userInfo.find { it.userId == balanceChangeRecord.userId }!!.apply {
                    this.userBalance = this.userBalance + balanceChangeRecord.balanceChange
                }
            }
        } catch (e: NullPointerException) {
            throw InvalidTransactionRecordException("Group with id $groupId does not contain " +
                    "all Users in transaction with id ${transactionRecord.getId()}")
        }
        groupRepository.updateGroup(group)
    }
}