package com.grup

import io.ktor.server.engine.*
import io.ktor.server.cio.*
import com.grup.plugins.*
import org.koin.core.context.GlobalContext.startKoin
import org.koin.logger.slf4jLogger

fun startServer() = main()

internal fun main() {
    startKoin {
        slf4jLogger()
        modules(listOf(
            repositoriesModule,
            servicesModule
        ))
    }
    embeddedServer(CIO, port = 8080, host = "0.0.0.0") {
        configureSerialization()
        configureRouting()
    }.start(wait = true)
}
