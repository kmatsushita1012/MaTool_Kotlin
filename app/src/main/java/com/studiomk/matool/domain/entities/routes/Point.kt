package com.studiomk.matool.domain.entities.routes

import com.studiomk.matool.core.others.Identifiable
import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Point(
    override val id: String,
    var coordinate: Coordinate,
    var title: String? = null,
    var description: String? = null,
    var time: SimpleTime? = null,
    val isPassed: Boolean = false,
    var shouldExport: Boolean = false
): Identifiable<String> {
    companion object
}


// サンプル
val Point.Companion.sample: Point
    get() = Point(
        id = UUID.randomUUID().toString(),
        coordinate = Coordinate.sample
    )