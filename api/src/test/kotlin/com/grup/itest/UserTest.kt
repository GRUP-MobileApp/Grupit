package com.grup.itest

import com.grup.plugins.configureSerialization
import com.grup.routes.userRouting
import com.grup.service.UserService
import com.grup.testRepositoriesModule
import io.ktor.client.request.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.dsl.module
import org.koin.logger.slf4jLogger
import org.koin.test.KoinTest
import org.koin.test.junit5.KoinTestExtension
import org.litote.kmongo.KMongo
import org.litote.kmongo.id.serialization.IdKotlinXSerializationModule
import org.litote.kmongo.json
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
@Testcontainers
class UserTest : KoinTest {
    @Container
    val container = MongoDBContainer(DockerImageName.parse("mongo:5.0.6"))

    init {
        container.start()
    }

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        slf4jLogger()
        modules(listOf(
            testRepositoriesModule,
            module {
                single { KMongo.createClient(container.replicaSetUrl) }
                single { UserService() }
            }
        ))
    }

    @Nested
    inner class CreateUser {
        @Test
        fun testBasicCreate() = testApplication {
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
                            serializersModule = IdKotlinXSerializationModule
                            ignoreUnknownKeys = true
                            isLenient = true
                            prettyPrint = true
                        },
                        contentType = ContentType.Application.Json
                    )
                }
            }
            val testName = "test"
            val response = client.post("user/create/$testName")
            Assertions.assertEquals(HttpStatusCode.OK, response.status)
            val user: JsonObject = Json.decodeFromString(response.json)
            Assertions.assertNotNull(user["id"])
            Assertions.assertEquals(testName, user["username"])
        }
    }
}