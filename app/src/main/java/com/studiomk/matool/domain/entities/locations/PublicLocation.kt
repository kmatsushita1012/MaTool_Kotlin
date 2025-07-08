package com.studiomk.matool.domain.entities.locations

import com.studiomk.matool.core.serialize.LocalDateTimeSerializer
import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Serializable
data class PublicLocation(
    val districtId: String,
    val districtName: String,
    val coordinate: Coordinate,
    @Serializable(with = LocalDateTimeSerializer::class)
    val timestamp: LocalDateTime
) {
    companion object
}

// サンプル
val PublicLocation.Companion.sample: PublicLocation
    get() = PublicLocation(
        districtId = "掛川祭_城北町",
        districtName = "城北町",
        coordinate = Coordinate.sample,
        timestamp = java.time.LocalDateTime.now()
    )