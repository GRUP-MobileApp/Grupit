package com.grup.models

import com.grup.objects.Id
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
class User : RealmObject {
    @PrimaryKey
    val id: Id = Id()
    var username: String? = null
}
