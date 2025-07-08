package com.studiomk.matool.domain.entities.routes

import com.studiomk.matool.domain.entities.shared.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class RouteSummary(
    val id: String,
    val districtId: String,
    val districtName: String,
    val date: SimpleDate,
    val title: String,
    val start: SimpleTime
) : Comparable<RouteSummary> {
    override fun compareTo(other: RouteSummary): Int {
        return if (date != other.date) {
            date.compareTo(other.date)
        } else {
            start.compareTo(other.start)
        }
    }

    companion object
}

fun RouteSummary.Companion.from(route: PublicRoute): RouteSummary =
    RouteSummary(
        id = route.id,
        districtId = route.districtId,
        districtName = route.districtName,
        date = route.date,
        title = route.title,
        start = route.start
    )

fun RouteSummary.text(format: String): String {
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

val RouteSummary.Companion.sample: RouteSummary
    get() = RouteSummary(
        id = UUID.randomUUID().toString(),
        districtId = "Johoku",
        districtName = "城北町",
        date = SimpleDate.sample,
        title = "午後",
        start = SimpleTime(9, 0)
    )