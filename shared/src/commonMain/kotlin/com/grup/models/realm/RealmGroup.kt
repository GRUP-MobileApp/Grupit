package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.Group
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey

@PersistedName("Group")
internal class RealmGroup : Group(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    override val groupName: String
        get() = _groupName ?: throw MissingFieldException("Group with id $_id missing inviter")

    @PersistedName("groupName")
    var _groupName: String? = null
}
