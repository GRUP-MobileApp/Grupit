package com.grup.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

@Serializable
class User(
    var username: String
) : BaseEntity() {
    @BsonId
    @Contextual
    val id: Id<User>? = null

    override fun getId(): String {
        return id.toString()
    }
}
