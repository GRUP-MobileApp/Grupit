package com.grup.other

import io.realm.kotlin.types.RealmInstant
import kotlinx.datetime.Clock
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

// Realm

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

internal fun getCurrentTime(): Instant = Clock.System.now()
