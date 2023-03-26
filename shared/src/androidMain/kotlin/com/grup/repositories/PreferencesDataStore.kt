package com.grup.repositories

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by
    preferencesDataStore(PreferencesDataStore.DATASTORE_NAME)

actual class PreferencesDataStore(
    private val context: Context
) {
    companion object {
        const val DATASTORE_NAME = "DataStore Name"
        const val GSO_LOGIN = "GSO Login"
    }

    actual suspend fun putString(key: String, value: String) {
        stringPreferencesKey(key).let { preferenceKey ->
            context.dataStore.edit {
                it[preferenceKey] = value
            }
        }
        context.dataStore.data.map { preferences ->
            preferences.asMap().keys.filter {
                it.name.startsWith("notification_")
            }
        }
    }

    actual suspend fun getString(key: String): String? {
        return try {
            stringPreferencesKey(key).let { preferenceKey ->
                context.dataStore.data.first()[preferenceKey]
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    actual fun getStringData(): Flow<Map<String, String>> {
        return context.dataStore.data.map { preferences ->
            preferences.asMap().filter { entry ->
                entry.value is String
            }.map { entry ->
                entry.key.name to (entry.value as String)
            }.toMap()
        }
    }

    actual suspend fun removeString(key: String) {
        stringPreferencesKey(key).let { preferenceKey ->
            context.dataStore.edit {
                if (it.contains(preferenceKey)) {
                    it.remove(preferenceKey)
                }
            }
        }
    }
}