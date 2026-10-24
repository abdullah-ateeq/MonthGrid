package com.example.monthgrid.model

import java.time.LocalDate
import java.time.YearMonth

data class MonthGridHistory(
    val id: String,
    val year: Int,
    val month: Int,
    val selectedDate: String?,
    val savedAt: Long
) {
    val yearMonth: YearMonth
        get() = YearMonth.of(year, month)

    val parsedSelectedDate: LocalDate?
        get() = selectedDate?.takeIf { it.isNotBlank() }?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }
}
