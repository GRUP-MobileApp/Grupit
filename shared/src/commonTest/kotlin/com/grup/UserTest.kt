package com.grup

import com.grup.controllers.UserController
import com.grup.models.User
import com.grup.service.UserService
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class UserTest : KoinTest {
    private val userController: UserController by inject()

    init {
        startKoin {
            modules(listOf(
                testRepositoriesModule,
                module {
                    single { UserService() }
                    single { UserController() }
                }
            ))
        }
    }

    @Test
    fun testCreateBasicUser() {
        val testUsername = "testCreateBasicUser"
        val user: User = createTestUser(testUsername)
        assertNotNull(user._id)
        assertEquals(testUsername, user.username)
    }

        @Test
    fun testGetUserByUsername() {
        val testUsername = "testGetUserByUsername"
        createTestUser(testUsername)

        val user: User = userController.getUserByUsername(testUsername)
        assertNotNull(user._id)
        assertEquals(testUsername, user.username)
    }

    private fun createTestUser(username: String): User {
        return userController.createUser(username)
    }
}