package com.grup.other

import com.grup.exceptions.NotFoundException
import com.grup.models.realm.RealmDebtAction
import com.grup.models.realm.RealmGroupInvite
import com.grup.models.realm.RealmSettleAction
import com.grup.models.realm.RealmTransactionRecord
import com.grup.models.realm.RealmUser
import com.grup.models.realm.RealmUserInfo
import io.realm.kotlin.MutableRealm
import io.realm.kotlin.ext.isValid
import io.realm.kotlin.query.RealmQuery
import io.realm.kotlin.types.BaseRealmObject
import io.realm.kotlin.types.RealmInstant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

internal fun RealmInstant.toInstant(): Instant {
    val sec: Long = this.epochSeconds
    val nano: Int = this.nanosecondsOfSecond

    return if (sec >= 0) { // For positive timestamps, conversion can happen directly
        Instant.fromEpochSeconds(sec, nano.toLong())
    } else {
        Instant.fromEpochSeconds(sec - 1, 1_000_000 + nano.toLong())
    }
}
internal fun Instant.toRealmInstant(): RealmInstant {
    val sec: Long = this.epochSeconds
    val nano: Int = this.nanosecondsOfSecond
    return if (sec >= 0) {
        RealmInstant.from(sec, nano)
    } else {
        RealmInstant.from(sec + 1, -1_000_000 + nano)
    }
}

internal object DateTimeToRealmInstantDeserializer : KSerializer<RealmInstant> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("RealmInstant", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: RealmInstant) {
        encoder.encodeString(value.toInstant().toLocalDateTime(TimeZone.UTC).toString())
    }

    override fun deserialize(decoder: Decoder): RealmInstant {
        return LocalDateTime.parse(decoder.decodeString().substringBefore('Z'))
            .toInstant(TimeZone.UTC).toRealmInstant()
    }
}

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
            (user as? RealmUser)?.resolve()
            group.resolve()
        }
        is RealmTransactionRecord -> {
            userInfo.resolve()
        }
        else -> { }
    }
}