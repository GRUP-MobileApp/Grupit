package com.grup.repositories

import kotlinx.coroutines.flow.Flow

actual class PreferencesDataStore {
    actual suspend fun putString(key: String, value: String) {
        TODO("Not yet implemented")
    }

    actual suspend fun getString(key: String): String? {
        TODO("Not yet implemented")
    }

    actual fun getStringData(): Flow<Map<String, String>> {
        TODO("Not yet implemented")
    }

    actual suspend fun removeString(key: String) {
        TODO("Not yet implemented")
    }
}