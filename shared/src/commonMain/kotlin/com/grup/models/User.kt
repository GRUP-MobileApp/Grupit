package com.grup.models

import com.grup.exceptions.MissingFieldException
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
class User internal constructor() : BaseEntity(), RealmObject {
    @PrimaryKey
    override var _id: String = createId()

    var username: String? = null
        get() = field ?: throw MissingFieldException("User with id $_id missing username")
        internal set

    internal constructor(realmId: String) : this() {
        this._id = realmId
    }
}
