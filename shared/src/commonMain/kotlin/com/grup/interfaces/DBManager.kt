package com.grup.interfaces

import com.grup.platform.signin.AuthManager

internal interface DBManager {
    val authProvider: AuthManager.AuthProvider

    suspend fun <T> startDBTransaction(transaction: () -> T): T
    suspend fun logOut()
    suspend fun close()
}