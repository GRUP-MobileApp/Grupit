package com.grup.itest

import com.grup.models.Group
import com.grup.plugins.configureSerialization
import com.grup.routes.groupRouting
import com.grup.service.GroupService
import com.grup.testRepositoriesModule
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
class GroupIt : KoinTest {
    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        slf4jLogger()
        modules(listOf(
            testRepositoriesModule,
            module {
                single { GroupService() }
            }
        ))
    }

    fun groupTestApplication(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        application {
            configureSerialization()
        }
        routing {
            groupRouting()
        }
        block()
    }

    @Nested
    inner class CreateGroup {
        @Test
        fun testCreateBasicGroup() = groupTestApplication {
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
            val testGroupName = "test_group"
            val response = client.post("group/create/$testGroupName")
            Assertions.assertEquals(HttpStatusCode.OK, response.status)
            val group: Group = response.body()
            Assertions.assertNotNull(group.id)
            Assertions.assertEquals(testGroupName, group.groupName)
            println("TESTING CREATE ${group.id} ${group.groupName}")
            client.close()
        }
    }

    @Nested
    inner class GetGroup {
        @Test
        fun testGetUserByUsername() = groupTestApplication {
            CreateGroup().testCreateBasicGroup()

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
            val testGroupName = "test_group"
            var response = client.post("group/create/$testGroupName")
            Assertions.assertEquals(HttpStatusCode.OK, response.status)
            val groupId: String = response.body<Group>().getId()
            println("GROUP BODY ${response.body<String>()}")

            response = client.get("group/$groupId")
            Assertions.assertEquals(HttpStatusCode.OK, response.status)
            val group: Group = response.body()
            Assertions.assertNotNull(group.id)
            Assertions.assertEquals(groupId, group.id)
            Assertions.assertEquals(testGroupName, group.groupName)
            client.close()
        }
    }
}