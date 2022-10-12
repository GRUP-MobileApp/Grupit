package com.grup.plugins

import com.grup.routes.groupRouting
import com.grup.routes.transactionRouting
import com.grup.routes.userBalanceRouting
import com.grup.routes.userRouting
import io.ktor.server.routing.*
import io.ktor.server.application.*

fun Application.configureRouting() {
    routing {
        userRouting()
        groupRouting()
        userBalanceRouting()
        transactionRouting()
    }
}
