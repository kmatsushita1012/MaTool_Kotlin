package com.studiomk.matool.core.serialize

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDateTime
import java.time.ZoneOffset

object LocalDateTimeSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.LONG)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val epochSecond = value.toEpochSecond(ZoneOffset.ofHours(9))
        encoder.encodeLong(epochSecond)
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val epochSecond = decoder.decodeLong()
        return LocalDateTime.ofEpochSecond(epochSecond, 0, ZoneOffset.ofHours(9))
    }
}
