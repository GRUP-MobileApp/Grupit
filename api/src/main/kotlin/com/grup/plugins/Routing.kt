package com.grup.plugins

import com.grup.routes.groupRouting
import com.grup.routes.transactionRouting
import com.grup.routes.userBalanceRouting
import com.grup.routes.userRouting

import io.ktor.http.*
import io.ktor.server.routing.*
import io.ktor.server.application.*
import io.ktor.server.response.*

internal fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Server up PogO", ContentType.Text.Plain)
        }

        userRouting()
        groupRouting()
        userBalanceRouting()
        transactionRouting()
    }
}
