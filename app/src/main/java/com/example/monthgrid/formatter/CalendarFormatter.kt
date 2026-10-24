package com.example.monthgrid.formatter

import com.example.monthgrid.model.CalendarDay
import com.example.monthgrid.model.CalendarWeek
import com.example.monthgrid.model.DateFormatOption
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class CalendarFormatter {

    fun formatMonthYear(
        yearMonth: YearMonth,
        locale: Locale = Locale.getDefault()
    ): String {
        val monthName = yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        return "$monthName ${yearMonth.year}"
    }

    fun formatMonthName(
        yearMonth: YearMonth,
        locale: Locale = Locale.getDefault()
    ): String {
        return yearMonth.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }

    fun formatWeekdayShort(
        dayOfWeek: DayOfWeek,
        locale: Locale = Locale.getDefault(),
        uppercase: Boolean = true
    ): String {
        val raw = dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, locale)
            .trimEnd('.')
            .take(3)
        return if (uppercase) {
            raw.uppercase(locale)
        } else {
            raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
        }
    }

    fun formatWeekdayFull(
        dayOfWeek: DayOfWeek,
        locale: Locale = Locale.getDefault()
    ): String {
        return dayOfWeek.getDisplayName(TextStyle.FULL, locale)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }

    fun formatDate(
        date: LocalDate,
        formatOption: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,
        locale: Locale = Locale.getDefault()
    ): String {
        val formatter = DateTimeFormatter.ofPattern(formatOption.pattern, locale)
        return date.format(formatter)
    }

    fun formatFullDateWithWeekday(
        date: LocalDate,
        formatOption: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,
        locale: Locale = Locale.getDefault()
    ): String {
        val weekday = formatWeekdayFull(date.dayOfWeek, locale)
        val formattedDate = formatDate(date, formatOption, locale)
        return "$weekday, $formattedDate"
    }

    fun formatShortDayAndMonth(
        date: LocalDate,
        locale: Locale = Locale.getDefault()
    ): String {
        val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM", locale)
        return date.format(formatter)
            .replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
    }

    fun formatDayAccessibilityDescription(
        day: CalendarDay,
        displayedMonth: YearMonth,
        locale: Locale = Locale.getDefault()
    ): String {
        val baseDateText = formatFullDateWithWeekday(
            date = day.date,
            formatOption = DateFormatOption.DAY_MONTH_YEAR,
            locale = locale
        )

        val qualifiers = mutableListOf<String>()
        if (!day.isCurrentMonth) {
            val dayYearMonth = YearMonth.from(day.date)
            if (dayYearMonth.isBefore(displayedMonth)) {
                qualifiers.add("previous month")
            } else if (dayYearMonth.isAfter(displayedMonth)) {
                qualifiers.add("next month")
            }
        }

        when {
            day.isToday && day.isSelected -> qualifiers.add("today and selected")
            day.isToday -> qualifiers.add("today")
            day.isSelected -> qualifiers.add("selected")
        }

        return if (qualifiers.isEmpty()) {
            "$baseDateText."
        } else {
            "$baseDateText, ${qualifiers.joinToString(", ")}."
        }
    }

    fun formatPlainTextCalendar(
        yearMonth: YearMonth,
        orderedWeekdays: List<DayOfWeek>,
        weeks: List<CalendarWeek>,
        showWeekNumbers: Boolean,
        locale: Locale = Locale.getDefault()
    ): String {
        val sb = StringBuilder()
        sb.appendLine(formatMonthYear(yearMonth, locale))
        sb.appendLine()

        val weekdayHeaders = orderedWeekdays.map {
            formatWeekdayShort(it, locale, uppercase = false).padStart(3, ' ')
        }

        if (showWeekNumbers) {
            sb.append("W   ")
        }
        sb.appendLine(weekdayHeaders.joinToString(" "))
        sb.appendLine()

        weeks.forEachIndexed { index, week ->
            if (showWeekNumbers) {
                val wkStr = (week.weekNumber?.toString() ?: "").padEnd(4, ' ')
                sb.append(wkStr)
            }
            val rowCells = week.days.map { day ->
                if (day.isCurrentMonth) {
                    day.date.dayOfMonth.toString().padStart(3, ' ')
                } else {
                    "   "
                }
            }
            sb.append(rowCells.joinToString(" "))
            if (index < weeks.lastIndex) {
                sb.appendLine()
            }
        }

        return sb.toString()
    }
}
