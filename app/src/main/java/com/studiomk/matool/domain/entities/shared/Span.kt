package com.studiomk.matool.domain.entities.shared

import com.studiomk.matool.core.serialize.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable
data class Span(
    val id: String,
    @Serializable(with = LocalDateTimeSerializer::class)
    val start: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val end: LocalDateTime
) : Comparable<Span> {
    override fun compareTo(other: Span): Int {
        return start.compareTo(other.start)
    }

    companion object
}

// サンプル
val Span.Companion.sample: Span
    get() = Span(
        id = UUID.randomUUID().toString(),
        start = LocalDateTime.now(),
        end = LocalDateTime.now()
    )

// テキスト変換
fun Span.text(year: Boolean = true): String {
    val dateFormatter = if (year) {
        DateTimeFormatter.ofPattern("yyyy/M/d").withLocale(Locale.JAPAN)
    } else {
        DateTimeFormatter.ofPattern("M/d").withLocale(Locale.JAPAN)
    }
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm").withLocale(Locale.JAPAN)

    val startDateOnly = start.toLocalDate()
    val endDateOnly = end.toLocalDate()

    return if (startDateOnly == endDateOnly) {
        "${start.format(dateFormatter)}  ${start.format(timeFormatter)}〜${end.format(timeFormatter)}"
    } else {
        "${start.format(dateFormatter)}  ${start.format(timeFormatter)}〜${end.format(dateFormatter)}  ${end.format(timeFormatter)}"
    }
}