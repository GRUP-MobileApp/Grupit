package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.exceptions.NotFoundException
import com.grup.models.Group
import com.grup.other.createId
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.types.BaseRealmObject
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey

@PersistedName("Group")
internal class RealmGroup() : Group(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    constructor(user: RealmUser): this() {
        _creatorId = user.id
    }

    override var groupName: String
        get() = _groupName ?: throw MissingFieldException("Group with id $_id missing inviter")
        set(value) { _groupName = value }

    @PersistedName("groupName")
    private var _groupName: String? = null
    @PersistedName("creatorId")
    private var _creatorId: String? = null
}
