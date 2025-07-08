package com.studiomk.matool.presentation.utils

import androidx.compose.material3.CalendarLocale
import androidx.compose.material3.DatePickerFormatter
import androidx.compose.material3.ExperimentalMaterial3Api
import java.time.Instant
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
class JapaneseDateFormatter : DatePickerFormatter {

    private val shortFormatter = DateTimeFormatter.ofPattern("M月 d日 (E)", Locale.JAPAN)
    private val mediumFormatter = DateTimeFormatter.ofPattern("yyyy年 M月 d日", Locale.JAPAN)
    private val monthYearFormatter = DateTimeFormatter.ofPattern("yyyy年 M月", Locale.JAPAN)

    override fun formatDate(
        dateMillis: Long?,
        locale: CalendarLocale,
        forContentDescription: Boolean
    ): String? {
        if (dateMillis == null) return null

        val zoneId = ZoneId.systemDefault()
        val date = Instant.ofEpochMilli(dateMillis).atZone(zoneId).toLocalDate()
        val today = LocalDate.now(zoneId)

        val formatter = if (date.year == today.year) shortFormatter else mediumFormatter
        return date.format(formatter)
    }

    override fun formatMonthYear(
        monthMillis: Long?,
        locale: CalendarLocale
    ): String? {
        if (monthMillis == null) return null

        val zoneId = ZoneId.systemDefault()
        val month = Instant.ofEpochMilli(monthMillis).atZone(zoneId).toLocalDate()
        val yearMonth = YearMonth.of(month.year, month.month)

        return yearMonth.format(monthYearFormatter)
    }
}
