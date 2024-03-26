package com.grup.interfaces

import com.grup.models.User
import com.grup.dbmanager.DatabaseManager.DatabaseWriteTransaction

internal interface IUserRepository : IRepository {
    fun createMyUser(
        transaction: DatabaseWriteTransaction,
        username: String,
        displayName: String,
        venmoUsername: String? = null
    ): User?

    fun findMyUser(): User?
    suspend fun findUserByUsername(username: String): User?

    fun updateUser(transaction: DatabaseWriteTransaction, user: User, block: User.() -> Unit): User
}