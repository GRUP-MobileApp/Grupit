package com.grup.repositories

expect class PreferencesDataStore {
    suspend fun putString(key: String, value: String)

    suspend fun getString(key: String): String?

    suspend fun removeString(key: String)
}
