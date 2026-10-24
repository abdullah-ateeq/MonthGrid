package com.example.monthgrid.domain

import com.example.monthgrid.calendar.CalendarCalculator
import com.example.monthgrid.generator.MonthGridGenerator
import com.example.monthgrid.model.CalendarWeek
import com.example.monthgrid.model.DateDetails
import com.example.monthgrid.model.MonthDetails
import com.example.monthgrid.model.MonthGridSettings
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

data class GeneratedMonthBundle(
    val yearMonth: YearMonth,
    val firstDayOfWeek: DayOfWeek,
    val orderedWeekdays: List<DayOfWeek>,
    val weeks: List<CalendarWeek>,
    val monthDetails: MonthDetails,
    val selectedDateDetails: DateDetails?,
    val selectedWeek: CalendarWeek?
)

class GenerateMonthGridUseCase(
    private val generator: MonthGridGenerator = MonthGridGenerator(),
    private val calculator: CalendarCalculator = CalendarCalculator()
) {
    fun execute(
        yearMonth: YearMonth,
        selectedDate: LocalDate?,
        settings: MonthGridSettings,
        today: LocalDate = LocalDate.now(),
        locale: Locale = Locale.getDefault()
    ): GeneratedMonthBundle {
        val firstDayOfWeek = calculator.resolveFirstDayOfWeek(settings.weekStart, locale)
        val weekFields = calculator.resolveWeekFields(settings.weekNumbering, locale)
        val weeks = generator.generate(
            yearMonth = yearMonth,
            firstDayOfWeek = firstDayOfWeek,
            today = today,
            selectedDate = selectedDate,
            weekFields = weekFields,
            includeWeekNumbers = true
        )
        val monthDetails = calculator.computeMonthDetails(yearMonth, weeks)
        val selectedDateDetails = selectedDate?.let { calculator.computeDateDetails(it, locale) }
        val selectedWeek = selectedDate?.let { calculator.findWeekContainingDate(it, weeks) }

        return GeneratedMonthBundle(
            yearMonth = yearMonth,
            firstDayOfWeek = firstDayOfWeek,
            orderedWeekdays = generator.getOrderedWeekdays(firstDayOfWeek),
            weeks = weeks,
            monthDetails = monthDetails,
            selectedDateDetails = selectedDateDetails,
            selectedWeek = selectedWeek
        )
    }
}
