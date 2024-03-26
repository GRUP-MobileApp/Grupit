package com.grup.models

import kotlinx.datetime.Instant

abstract class GroupInvite internal constructor() : BaseEntity() {
    abstract val inviter: User
    abstract val inviteeId: String
    abstract val group: Group
    abstract val date: Instant
}
