package com.grup.repositories

import com.grup.models.BaseEntity

// TODO
interface IRepository {
    fun create(model: BaseEntity): BaseEntity

    fun findById(id: String): BaseEntity
    fun exists(id: String): Boolean

    fun updateOne(entity: BaseEntity): BaseEntity

    fun deleteOne(entity: BaseEntity): BaseEntity
}