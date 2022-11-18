package com.grup.models

import com.grup.interfaces.IEntity
import com.grup.objects.Id

abstract class BaseEntity: IEntity {
    abstract var _id: Id
    override fun getId(): String {
        return _id
    }
}