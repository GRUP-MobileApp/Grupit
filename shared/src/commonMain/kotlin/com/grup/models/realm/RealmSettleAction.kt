package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.SettleAction
import com.grup.models.TransactionRecord
import com.grup.other.createId
import com.grup.other.getCurrentTime
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey

@PersistedName("SettleAction")
internal class RealmSettleAction : SettleAction(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    override val userInfo: RealmUserInfo
        get() = _userInfo
            ?: throw MissingFieldException("SettleAction with id $_id missing debtee")
    override val groupId: String
        get() = _groupId
            ?: throw MissingFieldException("SettleAction with id $_id missing groupId")
    override val transactionRecords: List<TransactionRecord>
        get() = _transactionRecords
    override val date: String
        get() = _date

    @PersistedName("userInfo")
    var _userInfo: RealmUserInfo? = null
    @PersistedName("groupId")
    var _groupId: String? = null
    @PersistedName("transactionRecords")
    var _transactionRecords: RealmList<RealmTransactionRecord> = realmListOf()
    @PersistedName("date")
    private var _date: String = getCurrentTime()
}
