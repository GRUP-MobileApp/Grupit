package com.grup

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.grup.plugins.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.logger.slf4jLogger

fun main() {
    startKoin {
        slf4jLogger()
        modules(listOf(
            repositoriesModule,
            servicesModule
        ))
    }
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
