package com.grup.routes

import com.grup.objects.ErrorResponse
import com.grup.models.User
import com.grup.service.UserService
import io.ktor.server.routing.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import org.koin.ktor.ext.inject

fun Route.userRouting() {
    val userService: UserService by inject()

    route("/user") {
        post("/create/{username}") {
            val username = call.parameters["username"].toString()

            if (userService.usernameExists(username)) {
                call.respond(HttpStatusCode.Conflict, ErrorResponse.USER_ALREADY_EXISTS)
            } else {
                val user = User(username = username)
                userService.createUser(user)
                    ?.let { createdUser ->
                        call.respond(createdUser)
                    } ?: call.respond(HttpStatusCode.BadRequest, ErrorResponse.BAD_REQUEST_RESPONSE)
            }
        }

        get("/{username}") {
            val username = call.parameters["username"].toString()

            userService.getUserByUsername(username)
                ?.let { foundUser ->
                    call.respond(foundUser)
                } ?: call.respond(HttpStatusCode.NotFound, ErrorResponse.NOT_FOUND_RESPONSE)
        }
    }
}
