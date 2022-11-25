package com.grup.models

import com.grup.interfaces.IEntity
import com.grup.objects.Id
import com.grup.objects.asString

abstract class BaseEntity: IEntity {
    abstract var _id: Id
    override fun getId(): String {
        return _id.asString()
    }
}