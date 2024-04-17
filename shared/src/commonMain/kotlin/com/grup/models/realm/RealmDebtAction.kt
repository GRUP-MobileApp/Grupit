package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.DebtAction
import com.grup.models.TransactionRecord
import com.grup.models.realm.RealmTransactionRecord.Companion.toRealmTransactionRecord
import com.grup.other.NestedRealmObject
import com.grup.other.createId
import com.grup.other.getLatest
import com.grup.other.toInstant
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Instant

@PersistedName("DebtAction")
internal class RealmDebtAction() : DebtAction(), RealmObject, NestedRealmObject {
    constructor(
        userInfo: RealmUserInfo,
        message: String,
        transactionRecords: List<TransactionRecord>,
        platform: Platform
    ) : this() {
        _userInfo = userInfo
        _groupId = userInfo.group.id
        _message = message
        _transactionRecords.addAll(transactionRecords.map { it.toRealmTransactionRecord() })
        _platform = platform.name
    }

    @PrimaryKey override var _id: String = createId()

    override val userInfo: RealmUserInfo
        get() = _userInfo
            ?: throw MissingFieldException("DebtAction with id $_id missing debteeUserInfo")
    override val message: String
        get() = _message
            ?: throw MissingFieldException("DebtAction with id $_id missing message")
    override val platform: Platform
        get() = Platform.valueOf(_platform)
    override val date: Instant
        get() = _date.toInstant()
    override val transactionRecords: List<RealmTransactionRecord>
        get() = _transactionRecords

    @PersistedName("userInfo")
    private var _userInfo: RealmUserInfo? = null
    @PersistedName("message")
    private var _message: String? = null
    @PersistedName("platform")
    private var _platform: String = Platform.Grupit.name
    @PersistedName("date")
    private var _date: RealmInstant = RealmInstant.now()
    @PersistedName("transactionRecords")
    private var _transactionRecords: RealmList<RealmTransactionRecord> = realmListOf()

    @PersistedName("groupId")
    private var _groupId: String? = null

    override fun getLatestFields(mutableRealm: MutableRealm) {
        with(mutableRealm) {
            _userInfo = getLatest(userInfo)
            _transactionRecords.forEachIndexed { i, transactionRecord ->
                _transactionRecords[i] = getLatest(transactionRecord)
            }
        }
    }
}