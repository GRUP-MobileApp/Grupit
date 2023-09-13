package com.grup.interfaces

import com.grup.platform.signin.AuthManager

internal interface DBManager {
    val authProvider: AuthManager.AuthProvider

    suspend fun startDBTransaction(transaction: () -> Unit)
    suspend fun close()
}