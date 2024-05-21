package com.grup.dbmanager

import com.grup.models.BaseEntity
import com.grup.platform.signin.AuthManager

internal sealed class DatabaseManager {
    abstract val authProvider: AuthManager.AuthProvider

    abstract inner class DatabaseWriteTransaction {
        abstract fun <T: BaseEntity> findObject(obj: T): T?

        abstract fun cancelWrite()
    }
    abstract suspend fun <T> write(transaction: DatabaseWriteTransaction.() -> T): T
    abstract suspend fun logOut()
    abstract suspend fun deleteUser()
}
