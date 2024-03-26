package com.grup.models

import com.grup.interfaces.IEntity

abstract class BaseEntity : IEntity {
    protected abstract var _id: String

    final override val id: String
        get() = _id
}
