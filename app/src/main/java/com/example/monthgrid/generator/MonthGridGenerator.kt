package com.example.monthgrid.generator

import com.example.monthgrid.model.CalendarDay
import com.example.monthgrid.model.CalendarWeek
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields

class MonthGridGenerator {

    @JvmOverloads
    fun generate(
        yearMonth: YearMonth,
        firstDayOfWeek: DayOfWeek,
        today: LocalDate = LocalDate.now(),
        selectedDate: LocalDate? = null,
        weekFields: WeekFields = WeekFields.ISO,
        includeWeekNumbers: Boolean = true
    ): List<CalendarWeek> {
        val firstOfMonth = yearMonth.atDay(1)
        val lastOfMonth = yearMonth.atEndOfMonth()

        val firstVisibleDate = firstOfMonth.with(TemporalAdjusters.previousOrSame(firstDayOfWeek))
        val lastDayOfWeek = firstDayOfWeek.plus(6)
        val lastVisibleDate = lastOfMonth.with(TemporalAdjusters.nextOrSame(lastDayOfWeek))

        val allDays = mutableListOf<CalendarDay>()
        var cursor = firstVisibleDate
        while (!cursor.isAfter(lastVisibleDate)) {
            allDays.add(
                CalendarDay(
                    date = cursor,
                    isCurrentMonth = YearMonth.from(cursor) == yearMonth,
                    isToday = cursor == today,
                    isSelected = cursor == selectedDate
                )
            )
            cursor = cursor.plusDays(1)
        }

        return allDays.chunked(7).map { weekDays ->
            val weekNumber = if (includeWeekNumbers) {
                calculateWeekNumber(weekDays, weekFields)
            } else {
                null
            }
            CalendarWeek(
                weekNumber = weekNumber,
                days = weekDays
            )
        }
    }

    fun calculateWeekNumber(
        weekDays: List<CalendarDay>,
        weekFields: WeekFields = WeekFields.ISO
    ): Int {
        if (weekDays.isEmpty()) return 1
        val representativeDate = if (weekFields == WeekFields.ISO) {
            weekDays.firstOrNull { it.date.dayOfWeek == DayOfWeek.THURSDAY }?.date
                ?: weekDays.firstOrNull { it.isCurrentMonth }?.date
                ?: weekDays.first().date
        } else {
            weekDays.firstOrNull { it.isCurrentMonth }?.date
                ?: weekDays[weekDays.size / 2].date
        }
        return representativeDate.get(weekFields.weekOfWeekBasedYear())
    }

    fun getOrderedWeekdays(firstDayOfWeek: DayOfWeek): List<DayOfWeek> {
        return (0L until 7L).map { offset -> firstDayOfWeek.plus(offset) }
    }
}
