package com.grup.repositories

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import com.grup.other.idSerialName
import kotlinx.coroutines.runBlocking
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

internal class UserRepository : IUserRepository {
    private val client = HttpClient(CIO) {
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

    override fun findUserById(realmUserId: String): User? {
        var responseUser: User? = null
        runBlocking {
            val response: HttpResponse = client.get(
                "${MONGODB_API_ENDPOINT}/user/findUserByRealmUserId"
            ) {
                contentType(ContentType.Application.Json)
                url {
                    parameters.append(idSerialName, realmUserId)
                }
            }
            if (response.status.value in 200..299) {
                responseUser = response.body()
            }
        }
        return responseUser
    }

    override fun findUserByUserName(username: String): User? {
        var responseUser: User? = null
        runBlocking {
            val response: HttpResponse = client.get(
                "${MONGODB_API_ENDPOINT}/user/findUserByUsername"
            ) {
                contentType(ContentType.Application.Json)
                url {
                    parameters.append("username", username)
                }
            }
            if (response.status.value in 200..299) {
                responseUser = response.body()
            }
        }
        return responseUser
    }

    override fun close() {
         client.close()
    }
}
