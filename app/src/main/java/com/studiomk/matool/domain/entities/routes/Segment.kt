package com.studiomk.matool.domain.entities.routes

import com.studiomk.matool.core.others.Identifiable
import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Segment(
    override val id: String,
    val start: Coordinate,
    val end: Coordinate,
    var coordinates: List<Coordinate>,
    val isPassed: Boolean = false
): Identifiable<String> {
    constructor(
        id: String,
        start: Coordinate,
        end: Coordinate,
        isPassed: Boolean = false
    ) : this(
        id = id,
        start = start,
        end = end,
        coordinates = listOf(start, end),
        isPassed = isPassed
    )

    companion object
}

// サンプル
val Segment.Companion.sample: Segment
    get() = Segment(
        id = UUID.randomUUID().toString(),
        start = Coordinate.sample,
        end = Coordinate.sample
    )