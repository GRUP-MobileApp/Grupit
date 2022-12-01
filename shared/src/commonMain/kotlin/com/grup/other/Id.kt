package com.grup.other

import io.realm.kotlin.types.RealmUUID

const val idSerialName = "_id"

fun createId(): String = RealmUUID.random().toString()

// Could just override toString
fun Id.asString() = toString()
