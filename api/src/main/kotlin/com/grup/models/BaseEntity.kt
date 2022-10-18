package com.grup.models

import com.grup.interfaces.IEntity
import com.grup.objects.Id
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
abstract class BaseEntity: IEntity, RealmObject {
    @PrimaryKey
    override val id: Id = Id()
    override fun getId(): String {
        return id.toString()
    }
}