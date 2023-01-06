package com.grup.controllers

import com.grup.exceptions.NotCreatedException
import com.grup.exceptions.NotFoundException
import com.grup.models.TransactionRecord
import com.grup.service.GroupService
import com.grup.service.TransactionRecordService
import com.grup.service.UserInfoService
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object TransactionRecordController : KoinComponent {
    private val userInfoService: UserInfoService by inject()
    private val transactionRecordService: TransactionRecordService by inject()
    private val groupService: GroupService by inject()

    fun createTransactionRecord(transactionRecord: TransactionRecord): TransactionRecord {
        groupService.getByGroupId(transactionRecord.groupId!!)
            ?: throw NotFoundException("Group with id ${transactionRecord.groupId} " +
                    "does not exist")
        //userInfoService.applyTransactionRecord(transactionRecord)
        return transactionRecordService.createTransactionRecord(transactionRecord)
            ?: throw NotCreatedException("Error creating TransactionRecord with id " +
                    transactionRecord.getId())
    }
}