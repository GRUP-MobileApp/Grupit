package com.grup.models

import com.grup.other.Id
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class UserInfo : BaseEntity(), RealmObject {
    @PrimaryKey override var _id: Id = createId()
    var userId: Id? = null
    var groupId: Id? = null
    var nickname: String? = null
    var userBalance: Double = 0.0
}
