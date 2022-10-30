package com.grup.objects

import io.realm.kotlin.types.ObjectId

typealias Id = String

const val idSerialName = "_id"

fun createId(): Id = ObjectId.create().toString()

fun createIdFromString(stringId: String): Id = ObjectId.from(stringId).toString()