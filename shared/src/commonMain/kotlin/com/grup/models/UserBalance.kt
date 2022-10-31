package com.grup.models

import com.grup.objects.Id
import com.grup.objects.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class UserBalance : BaseEntity(), RealmObject {
    @PrimaryKey
    override var _id: Id = createId()
    var groupId: Id? = null
    var userId: Id? = null
    var balance: Double? = null
}
