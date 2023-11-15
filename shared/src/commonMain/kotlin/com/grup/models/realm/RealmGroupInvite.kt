package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.GroupInvite
import com.grup.other.createId
import com.grup.other.getCurrentTime
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey

@PersistedName("GroupInvite")
internal class RealmGroupInvite : GroupInvite(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    override val inviter: RealmUser
        get() = _inviter ?: throw MissingFieldException("GroupInvite with id $_id missing inviter")
    override val inviteeId: String
        get() = _inviteeId
            ?: throw MissingFieldException("GroupInvite with id $_id missing inviteeId")
    override val group: RealmGroup
        get() = _group ?: throw MissingFieldException("GroupInvite with id $_id missing group")
    override val date: String
        get() = _date
    override val dateAccepted: String
        get() = _dateAccepted

    @PersistedName("inviter")
    var _inviter: RealmUser? = null
    @PersistedName("inviterId")
    var _inviterId: String? = null
    @PersistedName("inviteeId")
    var _inviteeId: String? = null
    @PersistedName("groupId")
    var _groupId: String? = null
    @PersistedName("group")
    var _group: RealmGroup? = null
    @PersistedName("date")
    var _date: String = getCurrentTime()
    @PersistedName("dateAccepted")
    var _dateAccepted: String = PENDING
}
