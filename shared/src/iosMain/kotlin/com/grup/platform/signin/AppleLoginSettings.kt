package com.grup.platform.signin

import com.grup.device.SettingsManager
import com.grup.exceptions.MissingFieldException

sealed class AppleSignInResult {
    internal companion object {
        const val STATUS_NAME = "appleSignInStatus"
        const val TOKEN_NAME = "appleToken"
        const val FULL_NAME = "appleFullName"
    }
    data object Failed : AppleSignInResult()
    data class Success(
        val appleToken: String,
        val fullName: String?
    ) : AppleSignInResult()
}

var SettingsManager.LoginSettings.appleSignInStatus
    get() = SettingsManager.settingsDataStore.getBoolean(AppleSignInResult.STATUS_NAME)?.let {
        if (it) {
            AppleSignInResult.Success(
                SettingsManager.settingsDataStore.getString(AppleSignInResult.TOKEN_NAME)
                    ?: throw MissingFieldException(),

                SettingsManager.settingsDataStore.getString(AppleSignInResult.FULL_NAME)
            )
        } else {
            AppleSignInResult.Failed
        }
    }
    set(value) {
        when (value) {
            is AppleSignInResult.Success -> {
                SettingsManager.settingsDataStore.putBoolean(AppleSignInResult.STATUS_NAME, true)
                SettingsManager.settingsDataStore.putString(
                    AppleSignInResult.TOKEN_NAME,
                    value.appleToken
                )
                if (value.fullName != null) {
                    SettingsManager.settingsDataStore.putString(
                        AppleSignInResult.FULL_NAME,
                        value.fullName
                    )
                } else {
                    SettingsManager.settingsDataStore.remove(AppleSignInResult.FULL_NAME)
                }
            }
            is AppleSignInResult.Failed -> {
                SettingsManager.settingsDataStore.putBoolean(AppleSignInResult.STATUS_NAME, false)
                SettingsManager.settingsDataStore.remove(AppleSignInResult.TOKEN_NAME)
                SettingsManager.settingsDataStore.remove(AppleSignInResult.FULL_NAME)
            }
            null -> {
                SettingsManager.settingsDataStore.remove(AppleSignInResult.STATUS_NAME)
                SettingsManager.settingsDataStore.remove(AppleSignInResult.TOKEN_NAME)
                SettingsManager.settingsDataStore.remove(AppleSignInResult.FULL_NAME)
            }
        }
    }