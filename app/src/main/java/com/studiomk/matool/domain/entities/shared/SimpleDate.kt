package com.studiomk.matool.domain.entities.shared

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Serializable
data class SimpleDate(
    val year: Int,
    val month: Int,
    val day: Int
){
    companion object
}

operator fun SimpleDate.compareTo(other: SimpleDate): Int {
    return when {
        this.year != other.year -> this.year - other.year
        this.month != other.month -> this.month - other.month
        else -> this.day - other.day
    }
}

@SuppressLint("DefaultLocale")
fun SimpleDate.text(format: String): String {
    val sb = StringBuilder()
    var i = 0

    while (i < format.length) {
        val char = format[i]
        val hasNext = i + 1 < format.length
        val nextChar = if (hasNext) format[i + 1] else null

        when (char) {
            'y' -> sb.append(year)
            'm' -> {
                if (nextChar == '2') {
                    sb.append(String.format("%02d", month))
                    i++
                } else {
                    sb.append(month)
                }
            }
            'd' -> {
                if (nextChar == '2') {
                    sb.append(String.format("%02d", day))
                    i++
                } else {
                    sb.append(day)
                }
            }
            else -> sb.append(char)
        }
        i++
    }

    return sb.toString()
}

fun SimpleDate.toDate(): LocalDateTime {
    return LocalDateTime.of(year, month, day,0,0)
}

fun SimpleDate.Companion.fromDate(date: LocalDateTime): SimpleDate {
    return SimpleDate(date.year, date.monthValue, date.dayOfMonth)
}

fun SimpleDate.toLocalDate(): LocalDate {
    return LocalDate.of(year, month, day)
}

fun SimpleDate.Companion.fromLocalDate(date: LocalDate): SimpleDate {
    return SimpleDate(date.year, date.monthValue, date.dayOfMonth)
}

val SimpleDate.Companion.today: SimpleDate
    get() = fromDate(LocalDateTime.now())

val SimpleDate.Companion.sample: SimpleDate
    get() = SimpleDate(2025, 10, 12)
