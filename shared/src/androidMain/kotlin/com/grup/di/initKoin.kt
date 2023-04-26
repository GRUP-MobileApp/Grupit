package com.grup.di

import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.module

actual fun initKoin(appDeclaration: (KoinApplication.() -> Unit)?) {
    startKoin {
        appDeclaration?.let { it() }
        modules(
            module {
                single {
                    HttpClient(OkHttp) {
                        install(ContentNegotiation) {
                            json(
                                Json {
                                    ignoreUnknownKeys = true
                                    isLenient = true
                                    prettyPrint = true
                                },
                                contentType = ContentType.Application.Json
                            )
                        }
                    }
                }
            }
        )
    }
}
