package com.studiomk.matool.domain.entities.districts

import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Performance(
    val id: String,
    var name: String = "",
    var performer: String = "",
    var description: String? = null
) {
    companion object
}

// サンプル
val Performance.Companion.sample: Performance
    get() = Performance(
        id = UUID.randomUUID().toString(),
        name = "ぽんぽこにゃ",
        performer = "小学校1,2年生",
        description = null
    )