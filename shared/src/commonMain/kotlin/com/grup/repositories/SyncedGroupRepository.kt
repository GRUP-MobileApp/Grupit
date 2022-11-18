package com.grup.repositories

import com.grup.models.Group
import com.grup.models.UserInfo
import io.realm.kotlin.mongodb.sync.SyncConfiguration

internal class SyncedGroupRepository(
    private val userManager: LoggedInUserManager
) : GroupRepository() {
    override val config =
        SyncConfiguration.Builder(userManager.realmUser, setOf(Group::class, UserInfo::class))
        .initialSubscriptions(rerunOnOpen = true) { realm ->
            add(
                realm.query(Group::class, "ANY userInfo.userId == $0", userManager.user._id)
            )
        }.waitForInitialRemoteData()
        .name("groupRealm")
        .build()

    override fun createGroup(group: Group): Group? {
        // Add yourself to the group
        group.userInfo.add(UserInfo().apply {
            this.userId = userManager.user._id
            this.username = userManager.user.username
            this.userBalance = 0.0
        })
        return super.createGroup(group)
    }
}