package com.grup.other

import com.grup.exceptions.NotFoundException
import com.grup.models.realm.RealmDebtAction
import com.grup.models.realm.RealmGroupInvite
import com.grup.models.realm.RealmSettleAction
import com.grup.models.realm.RealmTransactionRecord
import com.grup.models.realm.RealmUserInfo
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.types.BaseRealmObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal interface NestedRealmObject {
    fun getLatestFields(mutableRealm: MutableRealm)
}

internal fun <T: BaseRealmObject> MutableRealm.getLatest(obj: T): T =
    try {
        findLatest(obj) ?: throw NotFoundException("Object not in realm")
    } catch (e: IllegalArgumentException) {
        obj
    }.apply {
        if (this is NestedRealmObject) {
            getLatestFields(this@getLatest)
        }
    }

internal fun <T: BaseRealmObject> RealmQuery<T>
        .toResolvedListFlow(): Flow<List<T>> {
            return this.find().asFlow().map { resultsChange ->
                resultsChange.list.filter { obj ->
                    try {
                        obj.resolve()
                        true
                    } catch (e: Exception) {
                        false
                    }
                }
            }
        }

private fun BaseRealmObject.resolve() {
    if (!this.isValid()) {
        throw IllegalStateException("Unmanaged Realm object")
    }
    when (this) {
        is RealmGroupInvite -> {
            inviter.resolve()
            group.resolve()
        }
        is RealmSettleAction -> {
            userInfo.resolve()
            transactionRecords.forEach { transactionRecord ->
                (transactionRecord as RealmTransactionRecord).resolve()
            }
        }
        is RealmDebtAction -> {
            userInfo.resolve()
            transactionRecords.forEach { transactionRecord ->
                transactionRecord.userInfo.resolve()
            }
        }
        is RealmUserInfo -> {
            user.resolve()
            group.resolve()
        }
        is RealmTransactionRecord -> {
            userInfo.resolve()
        }
        else -> { }
    }
}