package com.grup.models

import com.grup.exceptions.MissingFieldException
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Group internal constructor() : BaseEntity(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    var groupName: String? = null
        get() = field ?: throw MissingFieldException("Group with id $_id missing inviter")
        internal set
}
