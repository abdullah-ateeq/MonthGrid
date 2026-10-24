package com.example.monthgrid.calendar

import com.example.monthgrid.model.CalendarWeek
import com.example.monthgrid.model.DateDetails
import com.example.monthgrid.model.MonthDetails
import com.example.monthgrid.model.WeekNumberingMode
import com.example.monthgrid.model.WeekStartOption
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

class CalendarCalculator {

    fun resolveFirstDayOfWeek(
        option: WeekStartOption,
        locale: Locale = Locale.getDefault()
    ): DayOfWeek {
        return when (option) {
            WeekStartOption.SYSTEM -> WeekFields.of(locale).firstDayOfWeek
            WeekStartOption.MONDAY -> DayOfWeek.MONDAY
            WeekStartOption.SUNDAY -> DayOfWeek.SUNDAY
        }
    }

    fun resolveWeekFields(
        mode: WeekNumberingMode,
        locale: Locale = Locale.getDefault()
    ): WeekFields {
        return when (mode) {
            WeekNumberingMode.LOCALE -> WeekFields.of(locale)
            WeekNumberingMode.ISO -> WeekFields.ISO
        }
    }

    fun computeDateDetails(
        date: LocalDate,
        locale: Locale = Locale.getDefault()
    ): DateDetails {
        val localeWeekFields = WeekFields.of(locale)
        val isoWeekFields = WeekFields.ISO
        val dayOfYear = date.dayOfYear
        val lengthOfYear = date.lengthOfYear()
        val daysRemaining = lengthOfYear - dayOfYear
        val yearMonth = YearMonth.from(date)

        return DateDetails(
            date = date,
            dayOfWeek = date.dayOfWeek,
            dayOfMonth = date.dayOfMonth,
            dayOfYear = dayOfYear,
            lengthOfYear = lengthOfYear,
            daysRemainingInYear = daysRemaining,
            localeWeekOfYear = date.get(localeWeekFields.weekOfWeekBasedYear()),
            isoWeekOfYear = date.get(isoWeekFields.weekOfWeekBasedYear()),
            isoWeekYear = date.get(isoWeekFields.weekBasedYear()),
            isLeapYear = date.isLeapYear,
            monthLength = yearMonth.lengthOfMonth()
        )
    }

    fun computeMonthDetails(
        yearMonth: YearMonth,
        calendarWeeks: List<CalendarWeek>
    ): MonthDetails {
        val firstDate = yearMonth.atDay(1)
        val lastDate = yearMonth.atEndOfMonth()
        return MonthDetails(
            yearMonth = yearMonth,
            daysInMonth = yearMonth.lengthOfMonth(),
            startsOn = firstDate.dayOfWeek,
            endsOn = lastDate.dayOfWeek,
            firstDate = firstDate,
            lastDate = lastDate,
            calendarRows = calendarWeeks.size,
            isLeapYear = yearMonth.isLeapYear
        )
    }

    fun findWeekContainingDate(
        date: LocalDate,
        calendarWeeks: List<CalendarWeek>
    ): CalendarWeek? {
        return calendarWeeks.firstOrNull { week ->
            week.days.any { it.date == date }
        }
    }
}
