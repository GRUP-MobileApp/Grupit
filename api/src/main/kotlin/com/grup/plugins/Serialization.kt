package com.grup.plugins

import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule = IdKotlinXSerializationModule
                ignoreUnknownKeys = true
                isLenient = true
                prettyPrint = true
            },
            contentType = ContentType.Application.Json
        )
    }
}
