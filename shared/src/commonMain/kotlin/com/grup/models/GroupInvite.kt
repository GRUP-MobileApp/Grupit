package com.grup.models

import com.grup.exceptions.MissingFieldException
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Clock

class GroupInvite internal constructor() : BaseEntity(), RealmObject {
    companion object {
        const val PENDING = "PENDING"
    }
    @PrimaryKey override var _id: String = createId()

    var inviter: String? = null
        get() = field ?: throw MissingFieldException("GroupInvite with id $_id missing inviter")
        internal set
    var inviterUsername: String? = null
        get() = field
            ?: throw MissingFieldException("GroupInvite with id $_id missing inviterUsername")
        internal set
    var invitee: String? = null
        get() = field ?: throw MissingFieldException("GroupInvite with id $_id missing invitee")
        internal set
    var inviteeUsername: String? = null
        get() = field
            ?: throw MissingFieldException("GroupInvite with id $_id missing inviteeUsername")
        internal set
    var groupId: String? = null
        get() = field ?: throw MissingFieldException("GroupInvite with id $_id missing groupId")
        internal set
    var groupName: String? = null
        get() = field ?: throw MissingFieldException("GroupInvite with id $_id missing groupName")
        internal set
    var date: String = Clock.System.now().toString()
        internal set
    var dateAccepted: String = PENDING
        internal set
}