package com.grup.repositories

import kotlinx.coroutines.flow.Flow

expect class PreferencesDataStore {
    suspend fun putString(key: String, value: String)

    suspend fun getString(key: String): String?
    fun getStringData(): Flow<Map<String, String>>

    suspend fun removeString(key: String)
}
