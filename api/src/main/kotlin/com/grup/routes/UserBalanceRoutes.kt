package com.grup.routes

import com.grup.objects.ErrorResponse
import com.grup.models.UserBalance
import com.grup.objects.Id
import com.grup.service.GroupService
import com.grup.service.UserBalanceService
import com.grup.service.UserService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

internal fun Route.userBalanceRouting() {
    val userBalanceService: UserBalanceService by inject()
    val userService: UserService by inject()
    val groupService: GroupService by inject()

    route("/group/{groupId}") {
        post("/add/{userId}") {
            val groupId = call.parameters["groupId"].toString()
            val userId = call.parameters["userId"].toString()

            if (!groupService.groupIdExists(groupId) || !userService.userIdExists(userId)) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
            }
            if (userBalanceService.userBalanceExists(groupId, userId)) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse.USER_ALREADY_EXISTS)
            } else {
                val userBalance: UserBalance = userBalanceService.createZeroUserBalance(groupId, userId)

                userBalanceService.createUserBalance(userBalance)
                    ?.let { createdUserBalance ->
                        call.respond(createdUserBalance)
                    } ?: call.respond(HttpStatusCode.BadRequest, ErrorResponse.BAD_REQUEST_RESPONSE)
            }
        }

        get("/get_balances") {
            val groupId = call.parameters["groupId"].toString()

            val userBalances: List<UserBalance> = userBalanceService.getUserBalancesByGroupId(groupId)
            if (userBalances.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
            } else {
                call.respond(userBalances)
            }
        }

        get("/get_users") {
            val groupId = call.parameters["groupId"].toString()

            val userIds: List<Id> = userBalanceService.getUserIdsByGroupId(groupId)
            if (userIds.isEmpty()) {
                call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
            } else {
                call.respond(userIds)
            }
        }
    }
}