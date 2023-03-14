package com.grup.repositories

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import com.grup.other.idSerialName
import kotlinx.coroutines.runBlocking
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.http.*
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class UserRepository : IUserRepository, KoinComponent {
    private val client: HttpClient by inject()
    override suspend fun findUserById(userId: String): User? {
        var responseUser: User? = null
        val response: HttpResponse = client.get(
            "${MONGODB_API_ENDPOINT}/user/findUserById"
        ) {
            contentType(ContentType.Application.Json)
            url {
                parameters.append(idSerialName, userId)
            }
        }
        if (response.status.value in 200..299) {
            responseUser = response.body()
        }
        return responseUser
    }

    override suspend fun findUserByUsername(username: String): User? {
        var responseUser: User? = null
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
        return responseUser
    }
}
