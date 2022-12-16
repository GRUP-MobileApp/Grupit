package com.grup.models

import com.grup.other.Id
import com.grup.other.createId
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class PendingRequest : BaseEntity(), RealmObject {
    enum class RequestType {
        GROUP_INVITE,
        FRIEND,
    }
    enum class RequestStatus {
        PENDING,
        ACCEPTED,
        REJECTED,
    }
    @PrimaryKey override var _id: Id = createId()
    var requester: Id? = null
    var target: Id? = null
    var requestType: RequestType? = null
    var status: RequestStatus = RequestStatus.PENDING
}