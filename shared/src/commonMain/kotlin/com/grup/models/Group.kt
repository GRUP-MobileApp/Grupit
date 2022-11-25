package com.grup.models

import com.grup.objects.Id
import com.grup.objects.createId
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class Group : BaseEntity(), RealmObject {
    class UserInfo : EmbeddedRealmObject {
        var userId: Id? = null
        var username: String? = null
        var userBalance: Double = 0.0
    }

    @PrimaryKey
    override var _id: Id = createId()
    var groupName: String? = null
    var users: RealmList<Id> = realmListOf()
    var userInfo: RealmList<UserInfo> = realmListOf()
}
