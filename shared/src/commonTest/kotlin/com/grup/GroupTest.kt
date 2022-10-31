package com.grup

import com.grup.controllers.GroupController
import com.grup.models.Group
import com.grup.service.GroupService
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.inject
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GroupTest : KoinTest {
    private val groupController: GroupController by inject()

    init {
        startKoin {
            modules(listOf(
                testRepositoriesModule,
                module {
                    single { GroupService() }
                    single { GroupController() }
                }
            ))
        }
    }

    @Test
    fun testCreateBasicGroup() {
        val testGroupName = "testCreateBasicGroup"
        val group: Group = createTestGroup(testGroupName)
        assertNotNull(group._id)
        assertEquals(testGroupName, group.groupName)
    }

    @Test
    fun testGetGroupById() {
        val testGroupName = "testGetGroupById"
        val testGroupId: String = createTestGroup(testGroupName).getId()

        val group: Group = groupController.getGroupById(testGroupId)
        assertNotNull(group._id)
        assertEquals(testGroupId, group._id)
        assertEquals(testGroupName, group.groupName)
    }

    private fun createTestGroup(groupName: String): Group {
        return groupController.createGroup(groupName)
    }
}