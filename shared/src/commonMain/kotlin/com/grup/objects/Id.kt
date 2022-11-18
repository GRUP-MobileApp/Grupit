package com.grup.objects

import io.realm.kotlin.types.RealmUUID

typealias Id = String

const val idSerialName = "_id"

fun createId(): Id = RealmUUID.random().toString()

fun createIdFromString(stringId: String): Id = RealmUUID.from(stringId).toString()