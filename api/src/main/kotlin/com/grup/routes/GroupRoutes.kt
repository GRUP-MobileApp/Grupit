package com.grup.routes

import com.grup.objects.ErrorResponse
import com.grup.models.Group
import com.grup.service.GroupService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.groupRouting() {
    val groupService: GroupService by inject()

    route("/group") {
        post("/create/{groupName}") {
            val groupName = call.parameters["groupName"].toString()
            val group = Group().apply {
                this.groupName = groupName
            }

            groupService.createGroup(group)
                ?.let { createdGroup ->
                    call.respond(createdGroup)
                } ?: call.respond(HttpStatusCode.BadRequest, ErrorResponse.BAD_REQUEST_RESPONSE)
        }

        get("/{groupId}") {
            val groupId = call.parameters["groupId"].toString()

            groupService.getByGroupId(groupId)
                ?.let { foundGroup ->
                    call.respond(foundGroup)
                } ?: call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
        }
    }
}