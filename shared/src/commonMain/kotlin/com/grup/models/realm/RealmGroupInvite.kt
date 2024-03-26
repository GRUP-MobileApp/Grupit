package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.GroupInvite
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

@PersistedName("GroupInvite")
internal class RealmGroupInvite() : GroupInvite(), RealmObject, NestedRealmObject {
    constructor(inviterUserInfo: RealmUserInfo, invitee: RealmUser): this() {
        _inviter = inviterUserInfo.user
        _inviterId = inviterUserInfo.user.id
        _inviteeId = invitee.id
        _group = inviterUserInfo.group
        _groupId = inviterUserInfo.group.id
    }

    @PrimaryKey override var _id: String = createId()

    override val inviter: RealmUser
        get() = _inviter
            ?: throw MissingFieldException("GroupInvite with id $_id missing inviter")
    override val inviteeId: String
        get() = _inviteeId
            ?: throw MissingFieldException("GroupInvite with id $_id missing inviteeId")
    override val group: RealmGroup
        get() = _group ?: throw MissingFieldException("GroupInvite with id $_id missing group")
    override val date: Instant
        get() = _date.toInstant()

    internal val inviterId: String
        get() = _inviterId
            ?: throw MissingFieldException("GroupInvite with id $_id missing inviterId")
    internal val groupId: String
        get() = _groupId ?: throw MissingFieldException("GroupInvite with id $_id missing groupId")

    @PersistedName("inviter")
    private var _inviter: RealmUser? = null
    @PersistedName("inviterId")
    private var _inviterId: String? = null
    @PersistedName("inviteeId")
    private var _inviteeId: String? = null
    @PersistedName("group")
    private var _group: RealmGroup? = null

    @PersistedName("groupId")
    private var _groupId: String? = null
    @PersistedName("date")
    private var _date: RealmInstant = RealmInstant.now()

    override fun getLatestFields(mutableRealm: MutableRealm) {
        with(mutableRealm) {
            _inviter = getLatest(inviter)
            _group = getLatest(group)
        }
    }
}
