package com.grup.models

import com.grup.interfaces.IEntity
import com.grup.objects.Id
import io.realm.kotlin.types.RealmObject

abstract class BaseEntity: IEntity, RealmObject {
    abstract var _id: Id
    override fun getId(): String {
        return _id
    }
}