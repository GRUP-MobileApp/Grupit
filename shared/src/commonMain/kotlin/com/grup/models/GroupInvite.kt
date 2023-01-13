package com.grup.models

import com.grup.exceptions.MissingFieldException
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class GroupInvite internal constructor() : BaseEntity(), RealmObject {
    object RequestStatus {
        const val PENDING = "PENDING"
        const val ACCEPTED = "ACCEPTED"
        const val REJECTED = "REJECTED"
    }
    @PrimaryKey override var _id: String = createId()

    var inviter: String? = null
        get() = field ?: throw MissingFieldException("GroupInvite with id $_id missing inviter")
        internal set
    var invitee: String? = null
        get() = field ?: throw MissingFieldException("GroupInvite with id $_id missing invitee")
        internal set
    var groupName: String? = null
        get() = field ?: throw MissingFieldException("GroupInvite with id $_id missing groupName")
        internal set
    var groupId: String? = null
        get() = field ?: throw MissingFieldException("GroupInvite with id $_id missing groupId")
        internal set
    var date: Instant = Clock.System.now()
        internal set
    var status: String = RequestStatus.PENDING
        internal set
}