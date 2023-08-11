package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.UserInfo
import com.grup.other.createId
import com.grup.other.getCurrentTime
import com.grup.other.idSerialName
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey

@PersistedName("UserInfo")
internal class RealmUserInfo(): UserInfo(), RealmObject {
    constructor(user: RealmUser) : this() {
        this._user = user
        this._userId = user.id
    }

    @PrimaryKey override var _id: String = createId()

    override val user: RealmUser
        get() = _user ?: throw MissingFieldException("UserInfo with id $_id missing User")
    override val groupId: String
        get() = _groupId ?: throw MissingFieldException("UserInfo with id $_id missing groupId")
    override var userBalance: Double
        get() = _userBalance
        set(value) { _userBalance = value }
    override val joinDate: String
        get() = _joinDate
    override var latestViewDate: String
        get() = _latestViewDate
        set(value) { _latestViewDate = value }

    @PersistedName("userId")
    var _userId: String? = null
    @PersistedName("user")
    var _user: RealmUser? = null
    @PersistedName("groupId")
    var _groupId: String? = null
    @PersistedName("userBalance")
    var _userBalance: Double = 0.0
    @PersistedName("joinDate")
    var _joinDate: String = getCurrentTime()
    @PersistedName("latestViewDate")
    var _latestViewDate: String = _joinDate
}
