package com.grup.repositories

import com.grup.interfaces.IUserBalanceRepository
import com.grup.models.UserBalance
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

internal class UserBalanceRepository(
    config: RealmConfiguration = RealmConfiguration.Builder(schema = setOf(UserBalance::class)).build()
) : IUserBalanceRepository {
    private val userBalanceRealm: Realm = Realm.open(config)

    override fun createUserBalance(userBalance: UserBalance): UserBalance? {
        return userBalanceRealm.writeBlocking {
            copyToRealm(userBalance)
        }
    }

    override fun findUserBalanceByUserAndGroupId(userId: String, groupId: String): UserBalance? {
        return userBalanceRealm.query(UserBalance::class,
            "userId == $userId && groupId == $groupId").first().find()
    }

    override fun findUserBalancesByGroupId(groupId: String): List<UserBalance> {
        return userBalanceRealm.query(UserBalance::class, "groupId == $groupId").find()
    }

    override fun updateUserBalance(newBalance: Double): UserBalance? {
        TODO("Not yet implemented")
    }

    override fun close() {
        userBalanceRealm.close()
    }
}