package com.grup.models.realm

import com.grup.exceptions.InvalidTransactionRecordException
import com.grup.exceptions.MissingFieldException
import com.grup.models.TransactionRecord
import com.grup.other.NestedRealmObject
import com.grup.other.getLatest
import com.grup.other.toInstant
import com.grup.other.toRealmInstant
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PersistedName
import kotlinx.datetime.Instant

@PersistedName("TransactionRecord")
internal class RealmTransactionRecord() :
    TransactionRecord(), EmbeddedRealmObject, NestedRealmObject {
    constructor(userInfo: RealmUserInfo, balanceChange: Double): this() {
        _userInfo = userInfo
        _balanceChange = balanceChange
    }

    @Ignore
    override var _id: String = super._id

    override val userInfo: RealmUserInfo
        get() = _userInfo
            ?: throw MissingFieldException("TransactionRecord missing debtorUserInfo")
    override var balanceChange: Double
        get() = _balanceChange
            ?: throw MissingFieldException("TransactionRecord missing balanceChange")
        set(value) { _balanceChange = value }
    override val dateCreated: Instant
        get() = _dateCreated.toInstant()

    override var status: Status
        get() = when(_status) {
            Status.ACCEPTED -> Status.Accepted(
                _dateAccepted?.toInstant()
                    ?: throw MissingFieldException("TransactionRecord missing dateAccepted")
            )
            Status.REJECTED -> Status.Rejected
            Status.PENDING -> Status.Pending
            else -> throw InvalidTransactionRecordException("TransactionRecord has invalid status")
        }
        set(value) {
            _status = value.status
            if (value is Status.Accepted) {
                _dateAccepted = value.date.toRealmInstant()
            }
        }

    @PersistedName("userInfo")
    private var _userInfo: RealmUserInfo? = null
    @PersistedName("balanceChange")
    private var _balanceChange: Double? = null
    @PersistedName("dateAccepted")
    private var _dateAccepted: RealmInstant? = null
    @PersistedName("status")
    private var _status: String = Status.Pending.status

    @PersistedName("dateCreated")
    private var _dateCreated: RealmInstant = RealmInstant.now()

    override fun getLatestFields(mutableRealm: MutableRealm) {
        with(mutableRealm) {
            _userInfo = getLatest(userInfo)
        }
    }

    companion object {
        fun TransactionRecord.toRealmTransactionRecord(): RealmTransactionRecord =
            RealmTransactionRecord(
                userInfo = this.userInfo as RealmUserInfo,
                balanceChange = balanceChange
            ).apply {
                with(this@toRealmTransactionRecord) {
                    _dateAccepted = (status as? Status.Accepted)?.date?.toRealmInstant()
                    _dateCreated = dateCreated.toRealmInstant()
                    _status = status.status
                }
            }
    }
}
