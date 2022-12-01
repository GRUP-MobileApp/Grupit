package com.grup.repositories

import com.grup.di.addGroup
import com.grup.models.Group
import com.grup.repositories.abstract.RealmGroupRepository
import io.realm.kotlin.Realm
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

internal class SyncedGroupRepository : RealmGroupRepository(), KoinComponent {
    override val realm: Realm by inject()

    override fun createGroup(group: Group): Group? {
        return realm.addGroup(group._id).run {
            super.createGroup(group)
        }
    }
}
