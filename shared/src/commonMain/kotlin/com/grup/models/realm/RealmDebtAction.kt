package com.grup.models.realm

import com.grup.exceptions.MissingFieldException
import com.grup.models.DebtAction
import com.grup.models.Group
import com.grup.models.TransactionRecord
import com.grup.other.createId
import com.grup.other.getCurrentTime
import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PersistedName
import io.realm.kotlin.types.annotations.PrimaryKey

@PersistedName("DebtAction")
internal class RealmDebtAction : DebtAction(), RealmObject {
    @PrimaryKey override var _id: String = createId()

    override val groupId: String
        get() = _groupId ?: throw MissingFieldException("DebtAction with id $_id missing groupId")
    override val userInfo: RealmUserInfo
        get() = _userInfo
            ?: throw MissingFieldException("DebtAction with id $_id missing debteeUserInfo")
    override val message: String
        get() = _message
            ?: throw MissingFieldException("DebtAction with id $_id missing message")
    override val date: String
        get() = _date
    override val transactionRecords: List<TransactionRecord>
        get() = _transactionRecords

    @PersistedName("groupId")
    var _groupId: String? = null
//    @PersistedName("group")
//    var _group: Group? = null
    @PersistedName("userInfo")
    var _userInfo: RealmUserInfo? = null
    @PersistedName("message")
    var _message: String? = null
    @PersistedName("transactionRecords")
    var _transactionRecords: RealmList<RealmTransactionRecord> = realmListOf()
    @PersistedName("date")
    private var _date: String = getCurrentTime()
}