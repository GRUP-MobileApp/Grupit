package com.grup.repositories

import com.grup.models.Group
import io.realm.kotlin.mongodb.sync.SyncConfiguration
import io.realm.kotlin.ext.query

internal class SyncedGroupRepository(
    private val userManager: LoggedInUserManager
) : GroupRepository() {
    override val config =
        SyncConfiguration.Builder(userManager.realmUser, setOf(Group::class, Group.UserInfo::class))
            .initialSubscriptions(rerunOnOpen = true) { realm ->
                add(realm.query<Group>("$0 IN users", userManager.user()._id))
            }
            .waitForInitialRemoteData()
            .name("groupRealm")
            .build()

    override fun createGroup(group: Group): Group? {
        // Add yourself to the group
        group.apply {
            this.users.add(userManager.user()._id)
            this.userInfo.add(Group.UserInfo().apply {
                this.userId = userManager.user()._id
                this.username = userManager.user().username
            })
        }
        return super.createGroup(group)
    }
}
