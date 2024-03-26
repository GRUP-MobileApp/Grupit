package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.UserInfo
import com.grup.other.NestedRealmObject
import com.grup.other.createId
import com.grup.other.getLatest
import com.grup.other.toInstant
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Instant

@PersistedName("UserInfo")
internal class RealmUserInfo() : UserInfo(), RealmObject, NestedRealmObject {
    constructor(user: RealmUser, group: RealmGroup): this() {
        _user = user
        _userId = user.id
        _group = group
        _groupId = group.id
    }

    @PrimaryKey override var _id: String = createId()

    override val user: RealmUser
        get() = _user ?: throw MissingFieldException("UserInfo with id $_id missing User")
    override val group: RealmGroup
        get() = _group ?: throw MissingFieldException("UserInfo with id $_id missing Group")
    override var userBalance: Double
        get() = _userBalance
        set(value) { _userBalance = value }
    override val joinDate: Instant
        get() = _joinDate.toInstant()

    internal val userId: String
        get() = _userId ?: throw MissingFieldException("UserInfo with id $_id missing userId")
    internal val groupId: String
        get() = _groupId ?: throw MissingFieldException("UserInfo with id $_id missing groupId")

    @PersistedName("userId")
    private var _userId: String? = null
    @PersistedName("user")
    private var _user: RealmUser? = null
    @PersistedName("groupId")
    private var _groupId: String? = null
    @PersistedName("group")
    private var _group: RealmGroup? = null
    @PersistedName("userBalance")
    private var _userBalance: Double = 0.0
    @PersistedName("joinDate")
    private var _joinDate: RealmInstant = RealmInstant.now()

    override fun getLatestFields(mutableRealm: MutableRealm) {
        with(mutableRealm) {
            _user = getLatest(user)
            _group = getLatest(group)
        }
    }
}
