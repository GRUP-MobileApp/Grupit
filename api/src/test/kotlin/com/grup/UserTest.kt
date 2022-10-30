package com.grup

import com.grup.controllers.UserController
import com.grup.models.User
import com.grup.service.UserService
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.junit5.KoinTestExtension

@TestInstance(value = TestInstance.Lifecycle.PER_CLASS)
class UserTest : KoinTest {
    private val userController: UserController by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(listOf(
            testRepositoriesModule,
            module {
                single { UserService() }
                single { UserController() }
            }
        ))
    }

    @Nested
    inner class CreateUser {
        @Test
        fun testCreateBasicUser() {
            val testUsername = "testCreateBasicUser"
            val user: User = createTestUser(testUsername)
            Assertions.assertNotNull(user._id)
            Assertions.assertEquals(testUsername, user.username)
        }
    }

    @Nested
    inner class GetUser {
        @Test
        fun testGetUserByUsername() {
            val testUsername = "testGetUserByUsername"
            createTestUser(testUsername)

            val user: User = userController.getUserByUsername(testUsername)
            Assertions.assertNotNull(user._id)
            Assertions.assertEquals(testUsername, user.username)
        }
    }

    private fun createTestUser(username: String): User {
        return userController.createUser(username)
    }
}