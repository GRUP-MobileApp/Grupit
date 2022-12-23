package com.grup.models

import com.grup.other.Id
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

class GroupInvite : BaseEntity(), RealmObject {
    enum class RequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
    }
    @PrimaryKey override var _id: Id = createId()
    var inviter: Id? = null
    var invitee: Id? = null
    var groupName: String? = null
    var groupId: Id? = null
    var date: Instant = Clock.System.now()
    var status: RequestStatus = RequestStatus.PENDING
}