package com.studiomk.matool.core.serialize

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

// 共通の小文字変換Serializer
abstract class LowerCaseEnumSerializer<T : Enum<T>>(
    private val enumValues: Array<T>
) : KSerializer<T> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LowerCaseEnum", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: T) {
        encoder.encodeString(value.name.lowercase())
    }

    override fun deserialize(decoder: Decoder): T {
        val name = decoder.decodeString().lowercase()
        return enumValues.firstOrNull { it.name.lowercase() == name }
            ?: throw IllegalArgumentException("Unknown enum value: $name")
    }
}