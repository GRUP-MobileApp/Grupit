package com.grup.models

import com.grup.objects.Id
import com.grup.objects.createId
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class User : BaseEntity(), RealmObject {
    @PrimaryKey
    override var _id: Id = createId()
    var realmId: String? = null
    var username: String? = null
    var groups: RealmList<Id> = realmListOf()
}
