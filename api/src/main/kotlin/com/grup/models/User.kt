package com.grup.models

import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable

@Serializable
class User : BaseEntity(), RealmObject {
    var username: String? = null
}
