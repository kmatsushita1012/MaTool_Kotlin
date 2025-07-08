package com.studiomk.matool.domain.entities.shared

import android.annotation.SuppressLint
import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Serializable
data class SimpleTime(
    val hour: Int,
    val minute: Int
) : Comparable<SimpleTime> {
    override fun compareTo(other: SimpleTime): Int {
        return when {
            this.hour != other.hour -> this.hour - other.hour
            else -> this.minute - other.minute
        }
    }
}

val SimpleTime.text: String
    @SuppressLint("DefaultLocale")
    get() = String.format("%02d:%02d", hour, minute)

fun SimpleTime.toDateTime(): LocalDateTime {
    val today = LocalDate.now()
    return LocalDateTime.of(today, LocalTime.of(hour, minute))
}

fun SimpleTime.Companion.fromDateTime(date: LocalDateTime): SimpleTime {
    return SimpleTime(date.hour, date.minute)
}

 fun SimpleTime.Companion.fromLocalTime(localTime: LocalTime): SimpleTime {
    return SimpleTime(
        hour = localTime.hour,
        minute = localTime.minute
    )
}

fun SimpleTime.toLocalTime(): LocalTime {
    return LocalTime.of(hour, minute)
}

val SimpleTime.Companion.sample: SimpleTime
    get() = SimpleTime(9, 0)