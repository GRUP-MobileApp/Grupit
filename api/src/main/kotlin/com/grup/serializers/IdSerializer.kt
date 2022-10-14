import com.grup.objects.Id
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class IdSerializer: KSerializer<Id> {
    override fun deserialize(decoder: Decoder): Id {
        return Id(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Id) {
        encoder.encodeString(value.toString())
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("id", PrimitiveKind.STRING)
}