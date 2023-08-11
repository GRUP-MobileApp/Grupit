package com.grup.models.realm

import com.grup.exceptions.ClassCastException
import com.grup.exceptions.MissingFieldException
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.models.TransactionRecord.Companion.DataTransactionRecord
import com.grup.other.createId
import com.grup.other.getCurrentTime
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.Ignore
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey

@PersistedName("SettleAction")
internal class RealmSettleAction : SettleAction(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    override val debteeUserInfo: RealmUserInfo
        get() = _debteeUserInfo
            ?: throw MissingFieldException("SettleAction with id $_id missing debtee")
    override val groupId: String
        get() = _groupId
            ?: throw MissingFieldException("SettleAction with id $_id missing groupId")
    @Ignore
    override val transactionRecords: MutableList<TransactionRecord> =
        object : AbstractMutableList<TransactionRecord>() {
            override fun get(index: Int): TransactionRecord {
                return _transactionRecords[index]
            }

            override fun add(index: Int, element: TransactionRecord) {
                if (element is DataTransactionRecord) {
                    _transactionRecords.add(index, element.toRealmTransactionRecord())
                } else {
                    throw ClassCastException("Expecting DataTransactionRecord")
                }
            }

            override fun removeAt(index: Int): TransactionRecord {
                return _transactionRecords.removeAt(index)
            }

            override fun set(index: Int, element: TransactionRecord): TransactionRecord {
                return if (element is DataTransactionRecord) {
                    _transactionRecords.set(index, element.toRealmTransactionRecord())
                } else {
                    throw ClassCastException("Expecting DataTransactionRecord")
                }
            }

            override val size: Int
                get() = _transactionRecords.size
        }

    override val date: String
        get() = _date

    override val settleAmount: Double
        get() = _settleAmount
            ?: throw MissingFieldException("SettleAction with id $_id missing settleAmount")

    @PersistedName("debteeUserInfo")
    var _debteeUserInfo: RealmUserInfo? = null
    @PersistedName("groupId")
    var _groupId: String? = null
    @PersistedName("transactionRecords")
    var _transactionRecords: RealmList<RealmTransactionRecord> = realmListOf()
    @PersistedName("date")
    private var _date: String = getCurrentTime()

    @PersistedName("settleAmount")
    var _settleAmount: Double? = null
}
