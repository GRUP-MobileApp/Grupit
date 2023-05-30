package com.grup.interfaces

import com.grup.platform.signin.AuthManager

internal interface DBManager {
    val authProvider: AuthManager.AuthProvider
    suspend fun close()
}