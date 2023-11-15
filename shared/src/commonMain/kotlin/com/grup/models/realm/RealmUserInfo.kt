package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.UserInfo
import com.grup.other.createId
import com.grup.other.getCurrentTime
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey

@PersistedName("UserInfo")
internal class RealmUserInfo : UserInfo(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    override val user: RealmUser
        get() = _user ?: throw MissingFieldException("UserInfo with id $_id missing User")
    override val group: RealmGroup
        get() = _group ?: throw MissingFieldException("UserInfo with id $_id missing groupId")
    override var userBalance: Double
        get() = _userBalance
        set(value) { _userBalance = value }
    override val joinDate: String
        get() = _joinDate

    @PersistedName("userId")
    var _userId: String? = null
    @PersistedName("user")
    var _user: RealmUser? = null
    @PersistedName("groupId")
    var _groupId: String? = null
    @PersistedName("group")
    var _group: RealmGroup? = null
    @PersistedName("userBalance")
    var _userBalance: Double = 0.0
    @PersistedName("joinDate")
    var _joinDate: String = getCurrentTime()
}
