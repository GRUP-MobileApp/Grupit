package com.grup.models

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.litote.kmongo.Id

@Serializable
class Group(
    var groupName: String
) : BaseEntity() {
    @BsonId
    @Contextual
    val id: Id<Group>? = null


    override fun getId(): String {
        return id.toString()
    }
}
