package com.grup.repositories

import com.grup.models.User
import com.grup.interfaces.IUserRepository
import kotlinx.coroutines.runBlocking
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.http.*

internal class UserRepository : IUserRepository {
    private val client = HttpClient(CIO)
    private val dataSource = "Cluster0"
    private val database = "GRUP"
    private val collection = "User"

    override fun findUserByUserName(username: String): User? {
        var responseUser: User? = null
        runBlocking {
            val response: HttpResponse = client.get(DATA_API_URL) {
                headers{
                    append("api-key", API_KEY)
                }
                contentType(ContentType.Application.Json)
                setBody("{'datasource': $dataSource, 'database': $database, 'collection': $collection, 'filter': {'username': $username}")
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
