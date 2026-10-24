package com.example.monthgrid.ui

import com.example.monthgrid.model.CalendarDay
import com.example.monthgrid.model.CalendarWeek
import com.example.monthgrid.model.DateDetails
import com.example.monthgrid.model.MonthDetails
import com.example.monthgrid.model.MonthGridHistory
import com.example.monthgrid.model.MonthGridSettings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class MonthGridUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate? = null,
    val calendarWeeks: List<CalendarWeek> = emptyList(),
    val monthDays: Int = YearMonth.now().lengthOfMonth(),
    val monthStartDay: DayOfWeek = YearMonth.now().atDay(1).dayOfWeek,
    val monthEndDay: DayOfWeek = YearMonth.now().atEndOfMonth().dayOfWeek,
    val history: List<MonthGridHistory> = emptyList(),
    val errorMessage: String? = null,
    val settings: MonthGridSettings = MonthGridSettings(),
    val orderedWeekdays: List<DayOfWeek> = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY,
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    ),
    val monthDetails: MonthDetails? = null,
    val selectedDateDetails: DateDetails? = null,
    val selectedWeek: CalendarWeek? = null,
    val statusMessage: String? = null
) {
    val weeksShown: Int
        get() = calendarWeeks.size

    val isCurrentMonthLeapYear: Boolean
        get() = currentMonth.isLeapYear
}
