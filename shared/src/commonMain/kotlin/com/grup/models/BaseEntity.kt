package com.grup.models

import com.grup.interfaces.IEntity
import com.grup.other.Id
import com.grup.other.asString

abstract class BaseEntity: IEntity {
    abstract var _id: Id
    override fun getId(): String {
        return _id.asString()
    }
}