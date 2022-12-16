package com.grup.models

import com.grup.other.Id
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User() : BaseEntity(), RealmObject {
    @PrimaryKey override var _id: Id = createId()
    var username: String? = null

    constructor(realmId: String) : this() {
        _id = realmId
    }
}
