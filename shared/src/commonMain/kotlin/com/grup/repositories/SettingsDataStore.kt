package com.grup.repositories

import com.grup.interfaces.ISettingsDataStore
import com.russhwolf.settings.Settings

internal class SettingsDataStore : ISettingsDataStore {
    private val settings: Settings = Settings()

    override fun putString(key: String, value: String) {
        settings.putString(key, value)
    }

    override fun putBoolean(key: String, value: Boolean) {
        settings.putBoolean(key, value)
    }

    override fun getString(key: String): String? {
        return settings.getStringOrNull(key)
    }

    override fun getBoolean(key: String): Boolean? {
        return settings.getBooleanOrNull(key)
    }

    override fun remove(key: String) {
        settings.remove(key)
    }
}
