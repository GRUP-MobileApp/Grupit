package com.grup.repositories

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import com.grup.other.MONGODB_API_ENDPOINT
import io.ktor.client.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal abstract class UserRepository : IUserRepository, KoinComponent {
    private val client: HttpClient by inject()

    override suspend fun findUserByUsername(username: String): User? {
        var responseUser: User? = null
        val response: HttpResponse = client.get(
            "$MONGODB_API_ENDPOINT/user/findUserByUsername"
        ) {
            contentType(ContentType.Application.Json)
            url {
                parameters.append("username", username)
            }
        }
        if (response.status.value in 200..299) {
            responseUser = try {
                Json.decodeFromString(response.bodyAsText())
            } catch (e: Exception) {
                null
            }
        }
        return responseUser
    }
}
