package com.grup.itest

import com.grup.models.User
import com.grup.plugins.configureSerialization
import com.grup.routes.userRouting
import com.grup.service.UserService
import com.grup.testRepositoriesModule
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class UserIT : KoinTest {
    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        slf4jLogger()
        modules(listOf(
            testRepositoriesModule,
            module {
                single { UserService() }
            }
        ))
    }

    fun userTestApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) = testApplication {
        application {
            configureSerialization()
        }
        routing {
            userRouting()
        }

        val client = createClient {
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
        block(client)
    }

    @Nested
    inner class CreateUser {
        @Test
        fun testCreateBasicUser() = userTestApplication { client ->
            val testUsername = "test_username"
            val response = client.post("user/create/$testUsername")
            Assertions.assertEquals(HttpStatusCode.OK, response.status)
            val user: User = response.body()
            Assertions.assertNotNull(user.id)
            Assertions.assertEquals(testUsername, user.username)
            client.close()
        }
    }

    @Nested
    inner class GetUser {
        @Test
        fun testGetUserByUsername() = userTestApplication { client ->
            val testUsername = "test_username"
            var response = client.post("user/create/$testUsername")
            Assertions.assertEquals(HttpStatusCode.OK, response.status)

            response = client.get("user/$testUsername")
            Assertions.assertEquals(HttpStatusCode.OK, response.status)
            val user: User = response.body()
            Assertions.assertNotNull(user.id)
            Assertions.assertEquals(testUsername, user.username)
            client.close()
        }
    }
}