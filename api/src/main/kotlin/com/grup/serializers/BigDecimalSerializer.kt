package com.grup.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.math.BigDecimal

class BigDecimalSerializer: KSerializer<BigDecimal> {
    override fun deserialize(decoder: Decoder): BigDecimal {
        return moneyRound(decoder.decodeString().toBigDecimal())
    }

    override fun serialize(encoder: Encoder, value: BigDecimal) {
        encoder.encodeString(moneyRound(value).toPlainString())
    }

    private fun moneyRound(value: BigDecimal): BigDecimal {
        return if (value.toDouble().rem(1) < 0.05) value.setScale(0) else value.setScale(2)
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("BigDecimal", PrimitiveKind.STRING)
}