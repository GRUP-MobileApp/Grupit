package com.grup.service

import com.grup.models.UserBalance
import com.grup.models.Group
import com.grup.models.User
import com.mongodb.client.MongoClient
import org.bson.types.ObjectId
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.litote.kmongo.*
import org.litote.kmongo.id.toId
import java.math.BigDecimal

class UserBalanceService : KoinComponent {
    private val client: MongoClient by inject()
    private val database = client.getDatabase("user_balance")
    private val userBalanceCollection = database.getCollection<UserBalance>()

    fun createUserBalance(userBalance: UserBalance): Id<UserBalance>? {
        userBalanceCollection.insertOne(userBalance)
        return userBalance.id
    }

    fun createZeroUserBalance(groupId: String, userId: String): UserBalance {
        val groupBsonId: Id<Group> = ObjectId(groupId).toId()
        val userBsonId: Id<User> = ObjectId(userId).toId()

        return UserBalance(groupId = groupBsonId, userId = userBsonId, balance = BigDecimal.ZERO)
    }

    fun getUserBalancesByGroupId(groupId: String): List<UserBalance> {
        val groupBsonId: Id<Group> = ObjectId(groupId).toId()

        return userBalanceCollection.find(UserBalance::groupId eq groupBsonId).toList()
    }

    fun getUserIdsByGroupId(groupId: String): List<Id<User>> {
        return getUserBalancesByGroupId(groupId).map { userBalance -> userBalance.userId }
    }

    fun userBalanceExists(groupId: String, userId: String): Boolean {
        val groupBsonId: Id<Group> = ObjectId(groupId).toId()
        val userBsonId: Id<User> = ObjectId(userId).toId()

        return userBalanceCollection.findOne {
            and(UserBalance::groupId eq groupBsonId, UserBalance::userId eq userBsonId) } != null
    }

    fun updateUserBalance(groupId: String, userId: String, balanceChange: BigDecimal): UserBalance? {
        val groupBsonId: Id<Group> = ObjectId(groupId).toId()
        val userBsonId: Id<User> = ObjectId(userId).toId()

        return userBalanceCollection.findOneAndUpdate(
            and(UserBalance::groupId eq groupBsonId, UserBalance::userId eq userBsonId),
            MongoOperator.add.from("[\"\$balance\", $balanceChange]") // NEED FIX
        )
    }
}