package com.grup.models

import com.grup.exceptions.MissingFieldException
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class UserInfo internal constructor() : BaseEntity(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    var userId: String? = null
        get() = field ?: throw MissingFieldException("UserInfo with id $_id missing userId")
        internal set
    var groupId: String? = null
        get() = field ?: throw MissingFieldException("UserInfo with id $_id missing groupId")
        internal set
    var nickname: String? = null
        get() = field ?: throw MissingFieldException("UserInfo with id $_id missing nickname")
        internal set
    var userBalance: Double = 0.0
        internal set
    var joinDate: String? = null
        get() = field ?: throw MissingFieldException("UserInfo with id $_id missing joinDate")
        internal set
}
