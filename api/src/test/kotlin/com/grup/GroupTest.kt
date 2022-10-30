package com.grup

import com.grup.controllers.GroupController
import com.grup.models.Group
import com.grup.service.GroupService
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
class GroupTest : KoinTest {
    private val groupController: GroupController by inject()

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(listOf(
            testRepositoriesModule,
            module {
                single { GroupService() }
                single { GroupController() }
            }
        ))
    }

    @Nested
    inner class CreateGroup {
        @Test
        fun testCreateBasicGroup() {
            val testGroupName = "testCreateBasicGroup"
            val group: Group = createTestGroup(testGroupName)
            Assertions.assertNotNull(group._id)
            Assertions.assertEquals(testGroupName, group.groupName)
        }
    }

    @Nested
    inner class GetGroup {
        @Test
        fun testGetGroupById() {
            val testGroupName = "testGetGroupById"
            val testGroupId: String = createTestGroup(testGroupName).getId()

            val group: Group = groupController.getGroupById(testGroupId)
            Assertions.assertNotNull(group._id)
            Assertions.assertEquals(testGroupId, group._id)
            Assertions.assertEquals(testGroupName, group.groupName)
        }
    }

    private fun createTestGroup(groupName: String): Group {
        return groupController.createGroup(groupName)
    }
}