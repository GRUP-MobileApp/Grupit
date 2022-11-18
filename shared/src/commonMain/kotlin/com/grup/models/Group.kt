package com.grup.models

import com.grup.objects.Id
import com.grup.objects.createId
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Group : BaseEntity(), RealmObject {
    @PrimaryKey
    override var _id: Id = createId()
    var groupName: String? = null
    var userInfo: RealmList<UserInfo> = realmListOf()
}
