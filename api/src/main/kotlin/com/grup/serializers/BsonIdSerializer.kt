package com.grup.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import org.bson.types.ObjectId
import org.litote.kmongo.Id
import org.litote.kmongo.id.toId

class BsonIdSerializer<T> : KSerializer<Id<T>> {

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("IdSerializer", PrimitiveKind.STRING)

    @Suppress("UNCHECKED_CAST")
    override fun deserialize(decoder: Decoder): Id<T> =
        ObjectId(decoder.decodeString()).toId()

    override fun serialize(encoder: Encoder, value: Id<T>) {
        encoder.encodeString(value.toString())
    }
}