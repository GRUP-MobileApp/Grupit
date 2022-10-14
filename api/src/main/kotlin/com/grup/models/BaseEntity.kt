package com.grup.models

import com.grup.interfaces.IEntity
import com.grup.objects.Id
import io.realm.kotlin.types.RealmObject
import kotlinx.serialization.Serializable

@Serializable
abstract class BaseEntity: IEntity, RealmObject {
    override val id: Id = Id()
    override fun getId(): String {
        return id.toString()
    }
}