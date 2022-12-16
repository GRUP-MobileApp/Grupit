package com.grup.repositories

import com.grup.exceptions.DoesNotExistException
import com.grup.models.User
import com.grup.interfaces.IUserRepository
import com.grup.other.Id
import com.grup.other.idSerialName
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.runBlocking
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.statement.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

internal class UserRepository : IUserRepository {
    private val client = HttpClient(CIO) {

    }
    private val key = "grup-app-qpcdr"
    override fun findUserByUserName(username: String): User? {
        val dataSource = "Cluster0"
        val database = "GRUP"
        val collection = "User"
        val responseUser = null;
        runBlocking {
            val response: HttpResponse = client.get("url"){
                headers{
                    append("api-key", key)
                }
                contentType(ContentType.Application.Json)
                setBody("{'datasource': $dataSource, 'database': $database, 'collection': $collection, 'filter': {'username': $username}")
            }
            if (response.status.value in 200..299) {
                val responseUser: User = response.body()
            }
        }
        return responseUser
    }

    override fun close() {
         client.close()
    }
}
