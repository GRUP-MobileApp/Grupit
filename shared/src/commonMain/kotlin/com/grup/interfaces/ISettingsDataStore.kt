package com.grup.interfaces

import kotlinx.coroutines.flow.Flow

internal interface ISettingsDataStore : IRepository {
    var userId: String
    fun putString(key: String, value: String)
    fun putBoolean(key: String, value: Boolean)

    fun getString(key: String): String?
    fun getBoolean(key: String): Boolean?

    fun remove(key: String)
}