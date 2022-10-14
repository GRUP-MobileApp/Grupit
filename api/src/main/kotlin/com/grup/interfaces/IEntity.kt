package com.grup.interfaces

import com.grup.objects.Id

interface IEntity {
    val id: Id
    fun getId(): String
}