package com.grup.repositories

import com.grup.interfaces.ISettingsDataStore
import com.russhwolf.settings.Settings

internal class SettingsDataStore : ISettingsDataStore {
    private val settings: Settings = Settings()

    override var userId: String
        get() = settings.getString("userId", "")
        set(value) { settings.putString("userId", value) }

    override fun putString(key: String, value: String) {
        settings.putString(userId + key, value)
    }

    override fun putBoolean(key: String, value: Boolean) {
        settings.putBoolean(userId + key, value)
    }

    override fun getString(key: String): String? {
        return settings.getStringOrNull(userId + key)
    }

    override fun getBoolean(key: String): Boolean? {
        return settings.getBooleanOrNull(userId + key)
    }

    override fun remove(key: String) {
        settings.remove(userId + key)
    }
}