package com.example.monthgrid.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark")
}

enum class WeekStartOption(val label: String) {
    SYSTEM("System"),
    MONDAY("Monday"),
    SUNDAY("Sunday")
}

enum class WeekNumberingMode(val label: String) {
    LOCALE("Locale"),
    ISO("ISO")
}

enum class CalendarDensity(val label: String) {
    COMPACT("Compact"),
    COMFORTABLE("Comfortable")
}

enum class DateFormatOption(val label: String, val pattern: String) {
    DAY_MONTH_YEAR("24 October 2026", "d MMMM yyyy"),
    SHORT_MONTH_DAY_YEAR("Oct 24, 2026", "MMM d, yyyy"),
    NUMERIC_DMY("24/10/2026", "dd/MM/yyyy"),
    ISO_DATE("2026-10-24", "yyyy-MM-dd")
}

data class MonthGridSettings(
    // Appearance
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,

    // Calendar Settings
    val weekStart: WeekStartOption = WeekStartOption.SYSTEM,
    val weekNumbering: WeekNumberingMode = WeekNumberingMode.LOCALE,
    val showWeekNumbers: Boolean = false,
    val showAdjacentMonthDays: Boolean = true,

    // Display Settings
    val calendarDensity: CalendarDensity = CalendarDensity.COMPACT,
    val showDayDetails: Boolean = true,
    val showWeekView: Boolean = true,
    val showMonthProgress: Boolean = false,
    val showLeapYear: Boolean = true,
    val showDayOfYear: Boolean = true,
    val showIsoWeek: Boolean = false,

    // Date Format
    val dateFormat: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,

    // Behavior Settings
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val confirmDelete: Boolean = true,
    val restoreLastMonth: Boolean = true
)

data class DateDetails(
    val date: LocalDate,
    val dayOfWeek: DayOfWeek,
    val dayOfMonth: Int,
    val dayOfYear: Int,
    val lengthOfYear: Int,
    val daysRemainingInYear: Int,
    val localeWeekOfYear: Int,
    val isoWeekOfYear: Int,
    val isoWeekYear: Int,
    val isLeapYear: Boolean,
    val monthLength: Int
)

data class MonthDetails(
    val yearMonth: YearMonth,
    val daysInMonth: Int,
    val startsOn: DayOfWeek,
    val endsOn: DayOfWeek,
    val firstDate: LocalDate,
    val lastDate: LocalDate,
    val calendarRows: Int,
    val isLeapYear: Boolean
)
