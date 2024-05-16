package com.grup.repositories

import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction
import com.grup.dbmanager.RealmManager
import com.grup.models.realm.RealmUser
import com.grup.other.MONGODB_API_ENDPOINT
import com.grup.other.TEST_MONGODB_API_ENDPOINT
import com.grup.repositories.abstract.RealmUserRepository
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.mongodb.subscriptions
import io.realm.kotlin.mongodb.sync.asQuery
import io.realm.kotlin.mongodb.syncSession
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedUserRepository(
    private val isDebug: Boolean = false
) : KoinComponent, RealmUserRepository() {
    override val realm: Realm by inject()
    private val client: HttpClient by inject()

    override fun createMyUser(
        transaction: DatabaseWriteTransaction,
        username: String,
        displayName: String,
        venmoUsername: String?
    ): RealmUser = with(transaction as RealmManager.RealmWriteTransaction) {
        copyToRealm(
            RealmUser(realm.syncSession.user, username).apply {
                this.displayName = displayName
                this.venmoUsername = venmoUsername ?: "None"
            },
            UpdatePolicy.ERROR
        )
    }

    override fun findMyUser(): RealmUser? {
        return realm.subscriptions.findByName("MyUser")!!.asQuery<RealmUser>().first().find()
            ?: runBlocking { findMyUser(realm.syncSession.user.id) }
    }

    override suspend fun findUserByUsername(username: String): RealmUser? {
        var responseUser: RealmUser? = null
        val response: HttpResponse = client.get(
            (if (isDebug) TEST_MONGODB_API_ENDPOINT else MONGODB_API_ENDPOINT) +
                    "/user/findUserByUsername"
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
                println("RealmUser decode error: ${e.message}")
                null
            }
        }
        return responseUser
    }

    private suspend fun findMyUser(userId: String): RealmUser? {
        var responseUser: RealmUser? = null
        val response: HttpResponse = client.get(
            (if (isDebug) TEST_MONGODB_API_ENDPOINT else MONGODB_API_ENDPOINT) +
                    "/user/findUserById"
        ) {
            contentType(ContentType.Application.Json)
            url {
                parameters.append("id", userId)
            }
        }
        if (response.status.value in 200..299) {
            responseUser = try {
                Json.decodeFromString(response.bodyAsText())
            } catch (e: Exception) {
                println("RealmUser decode error: ${e.message}")
                null
            }
        }
        return responseUser
    }
}
