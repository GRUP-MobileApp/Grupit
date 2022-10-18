package com.grup.models

import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable

@Serializable
class Group : BaseEntity(), RealmObject {
    var groupName: String? = null
}
