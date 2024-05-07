package com.grup.other

import com.grup.interfaces.ISettingsDataStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

object FirstTimeSettings : KoinComponent {
    private val settingsDataStore: ISettingsDataStore by inject()

    var hasViewedTutorial: Boolean
        get() = settingsDataStore.getBoolean("Tutorial") == true
        set(value) { settingsDataStore.putBoolean("Tutorial", value) }
}