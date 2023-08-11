package com.grup.repositories

import com.grup.models.realm.RealmUser
import com.grup.other.MONGODB_API_ENDPOINT
import com.grup.other.TEST_MONGODB_API_ENDPOINT
import com.grup.other.copyNestedObjectToRealm
import com.grup.repositories.abstract.RealmUserRepository
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.realm.kotlin.Realm
import io.realm.kotlin.mongodb.syncSession
import kotlinx.serialization.json.Json
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedUserRepository(
    private val isDebug: Boolean = false
) : KoinComponent, RealmUserRepository() {
    override val realm: Realm by inject()
    private val client: HttpClient by inject()

    private val myUserId: String
        get() = realm.syncSession.user.id

    override suspend fun createMyUser(
        username: String,
        displayName: String
    ): RealmUser {
        return realm.write {
            copyNestedObjectToRealm(
                RealmUser(myUserId).apply {
                    this._username = username
                    this._displayName = displayName
                }
            )
        }.also {
            realm.syncSession.uploadAllLocalChanges()
            realm.syncSession.downloadAllServerChanges()
        }
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
}
