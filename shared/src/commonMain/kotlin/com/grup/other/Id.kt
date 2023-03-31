package com.grup.other

import io.realm.kotlin.types.RealmUUID

internal const val idSerialName = "_id"

internal fun createId(): String = RealmUUID.random().toString()
