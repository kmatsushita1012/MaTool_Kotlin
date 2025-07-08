package com.studiomk.matool.domain.entities.routes

import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Route(
    val id: String,
    val districtId: String,
    var date: SimpleDate = SimpleDate.today,
    var start: SimpleTime,
    var goal: SimpleTime,
    var title: String = "",
    var description: String? = null,
    var points: List<Point> = emptyList<Point>(),
    var segments: List<Segment> = emptyList<Segment>(),
) {
    companion object
}

// テキストプロパティ（現状空文字返却）
val Route.text: String
    get() = ""

// サンプル
val Route.Companion.sample: Route
    get() = Route(
        id = UUID.randomUUID().toString(),
        districtId = "Johoku",
        date = SimpleDate.sample,
        title = "午後",
        description = "準備中",
        points = mutableListOf(
            Point(
                id = UUID.randomUUID().toString(),
                coordinate = Coordinate(34.777681, 138.007029),
                title = "出発",
                time = SimpleTime(9, 0)
            ),
            Point(
                id = UUID.randomUUID().toString(),
                coordinate = Coordinate(34.778314, 138.008176),
                title = "到着",
                description = "お疲れ様です",
                time = SimpleTime(12, 0)
            )
        ),
        segments = mutableListOf(
            Segment(
                id = UUID.randomUUID().toString(),
                start = Coordinate(34.777681, 138.007029),
                end = Coordinate(34.778314, 138.008176),
                coordinates = listOf(
                    Coordinate(34.777681, 138.007029),
                    Coordinate(34.777707, 138.008183),
                    Coordinate(34.778314, 138.008176)
                ),
                isPassed = true
            )
        ),
        start = SimpleTime.sample,
        goal = SimpleTime(12, 0)
    )