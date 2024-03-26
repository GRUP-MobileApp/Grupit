package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.SettleAction
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
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey
import kotlinx.datetime.Instant

@PersistedName("SettleAction")
internal open class RealmSettleAction() : SettleAction(), RealmObject, NestedRealmObject {
    constructor(userInfo: RealmUserInfo, amount: Double) : this() {
        _userInfo = userInfo
        _groupId = userInfo.group.id
        _amount = amount
    }

    @PrimaryKey final override var _id: String = createId()

    final override val userInfo: RealmUserInfo
        get() = _userInfo
            ?: throw MissingFieldException("SettleAction with id $_id missing debtee")
    @Ignore
    override val transactionRecords: MutableList<TransactionRecord> =
        // Real implementation inside RealmSettleActionRepository
        object : AbstractMutableList<TransactionRecord>() {
            override fun get(index: Int): RealmTransactionRecord = _transactionRecords[index]

            override fun add(index: Int, element: TransactionRecord) =
                _transactionRecords.add(index, element.toRealmTransactionRecord())

            override fun removeAt(index: Int): RealmTransactionRecord =
                _transactionRecords.removeAt(index)

            override fun set(index: Int, element: TransactionRecord) =
                _transactionRecords.set(index, element.toRealmTransactionRecord())
            override val size: Int
                get() = _transactionRecords.size
        }
    final override val date: Instant
        get() = _date.toInstant()

    final override var amount: Double
        get() = _amount
            ?: throw MissingFieldException("SettleAction with id $_id missing amount")
        set(value) { _amount = value }

    @PersistedName("userInfo")
    private var _userInfo: RealmUserInfo? = null
    @PersistedName("transactionRecords")
    protected var _transactionRecords: RealmList<RealmTransactionRecord> = realmListOf()
    @PersistedName("amount")
    private var _amount: Double? = null

    @PersistedName("groupId")
    private var _groupId: String? = null
    @PersistedName("date")
    private var _date: RealmInstant = RealmInstant.now()
    final override fun getLatestFields(mutableRealm: MutableRealm) {
        with(mutableRealm) {
            _userInfo = getLatest(userInfo)
            _transactionRecords.forEachIndexed { i, transactionRecord ->
                _transactionRecords[i] = getLatest(transactionRecord)
            }
        }
    }
}
