package com.grup.other

import com.grup.models.*
import io.realm.kotlin.MutableRealm

internal fun <T: BaseEntity> MutableRealm.getLatestFields(obj: T): T {
    when(obj) {
        is SettleAction -> {
            obj.debteeUserInfo = findLatest(obj.debteeUserInfo!!)
            obj.transactionRecords.forEachIndexed { i, _ ->
                obj.transactionRecords[i].apply {
                    this.debtorUserInfo = findLatest(this.debtorUserInfo!!)!!
                }
            }
        }
        is DebtAction -> {
            obj.debteeUserInfo = findLatest(obj.debteeUserInfo!!)
            obj.transactionRecords.forEachIndexed { i, _ ->
                obj.transactionRecords[i].apply {
                    this.debtorUserInfo = findLatest(this.debtorUserInfo!!)!!
                }
            }
        }
    }
    return obj
}
