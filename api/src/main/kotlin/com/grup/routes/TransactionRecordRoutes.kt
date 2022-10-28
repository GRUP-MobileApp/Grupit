package com.grup.routes

import com.grup.models.TransactionRecord
import com.grup.objects.ErrorResponse
import com.grup.service.GroupService
import com.grup.service.TransactionRecordService
import com.grup.service.UserBalanceService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

internal fun Route.transactionRouting() {
    val transactionRecordService: TransactionRecordService by inject()
    val groupService: GroupService by inject()
    val userBalanceService: UserBalanceService by inject()

    route("/transaction") {
        post("/add") {
            val transactionRecord = call.receive<TransactionRecord>()
            val groupId = transactionRecord.groupId.toString()
            if(transactionRecord.balanceChanges == null) {
                return@post
            }
            groupService.getByGroupId(groupId)
                ?.let {
                    if (!userBalanceService.getUserIdsByGroupId(groupId).containsAll(
                            transactionRecord.balanceChanges.map {
                                    balanceChangeRecord -> balanceChangeRecord.userId
                            })) {
                        call.respond(HttpStatusCode.Conflict, ErrorResponse.BAD_TRANSACTION_RECORD)
                    } else {
                        transactionRecord.balanceChanges.forEach { balanceChangeRecord ->
                            userBalanceService.updateUserBalance(
                                groupId,
                                balanceChangeRecord.userId.toString(),
                                balanceChangeRecord.balanceChange
                            )
                        }
                        transactionRecordService.createTransactionRecord(transactionRecord)
                            ?.let {
                                call.respond(userBalanceService.getUserBalancesByGroupId(groupId))
                            } ?: call.respond(HttpStatusCode.BadRequest, ErrorResponse.BAD_REQUEST_RESPONSE)
                    }
                } ?: call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
        }
    }
}