package com.studiomk.matool.domain.entities.routes

import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class PublicRoute(
    val id: String,
    val districtId: String,
    val districtName: String,
    val date: SimpleDate,
    val title: String,
    val description: String? = null,
    val points: List<Point>,
    val segments: List<Segment>,
    val start: SimpleTime,
    val goal: SimpleTime
) {
    companion object
}

// テキスト変換
fun PublicRoute.text(format: String): String {
    val sb = StringBuilder()
    var i = 0
    while (i < format.length) {
        val char = format[i]
        val hasNext = i + 1 < format.length
        val nextChar = if (hasNext) format[i + 1] else null

        when (char) {
            'D' -> sb.append(districtName)
            'T' -> sb.append(title)
            'y' -> sb.append(date.year)
            'm' -> {
                if (nextChar == '2') {
                    sb.append(String.format("%02d", date.month))
                    i++
                } else {
                    sb.append(date.month)
                }
            }
            'd' -> {
                if (nextChar == '2') {
                    sb.append(String.format("%02d", date.day))
                    i++
                } else {
                    sb.append(date.day)
                }
            }
            else -> sb.append(char)
        }
        i++
    }
    return sb.toString()
}

// Routeへの変換
fun PublicRoute.toModel(): Route = Route(
    id = id,
    districtId = districtId,
    date = date,
    title = title,
    description = description,
    points =  points.toMutableList(),
    segments = segments.toMutableList(),
    start = start,
    goal = goal
)

// RouteからPublicRouteを生成
fun PublicRoute.Companion.fromRoute(route: Route, name: String): PublicRoute =
    PublicRoute(
        id = route.id,
        districtId = route.districtId,
        districtName = name,
        date = route.date,
        title = route.title,
        description = route.description,
        points = route.points,
        segments = route.segments,
        start = route.start,
        goal = route.goal
    )

// サンプル
val PublicRoute.Companion.sample: PublicRoute
    get() = PublicRoute(
        id = UUID.randomUUID().toString(),
        districtId = "Johoku",
        districtName = "城北町",
        date = SimpleDate.sample,
        title = "午後",
        description = "省略",
        points = listOf(
            Point(
                id = UUID.randomUUID().toString(),
                coordinate = Coordinate(34.777681, 138.007029),
                title = "出発",
                time = SimpleTime(9, 0),
                isPassed = true
            ),
            Point(
                id = UUID.randomUUID().toString(),
                coordinate = Coordinate(34.778314, 138.008176),
                title = "到着",
                description = "お疲れ様です",
                time = SimpleTime(12, 0),
                isPassed = true
            )
        ),
        segments = listOf(
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