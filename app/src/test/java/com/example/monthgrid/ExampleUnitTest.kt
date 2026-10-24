package com.example.monthgrid

import com.example.monthgrid.calendar.CalendarCalculator
import com.example.monthgrid.data.InMemoryMonthGridStorage
import com.example.monthgrid.formatter.CalendarFormatter
import com.example.monthgrid.generator.MonthGridGenerator
import com.example.monthgrid.history.HistoryManager
import com.example.monthgrid.model.CalendarDay
import com.example.monthgrid.model.MonthGridSettings
import com.example.monthgrid.model.WeekNumberingMode
import com.example.monthgrid.model.WeekStartOption
import com.example.monthgrid.repository.MonthGridRepository
import com.example.monthgrid.ui.MonthGridViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

class ExampleUnitTest {

    private val generator = MonthGridGenerator()
    private val calculator = CalendarCalculator()
    private val formatter = CalendarFormatter()

    @Test
    fun basicMonth_october2026_has31DaysStartsThursdayEndsSaturdayAndCorrectAlignment() {
        val oct2026 = YearMonth.of(2026, Month.OCTOBER)
        assertEquals(31, oct2026.lengthOfMonth())
        assertEquals(DayOfWeek.THURSDAY, oct2026.atDay(1).dayOfWeek)
        assertEquals(DayOfWeek.SATURDAY, oct2026.atEndOfMonth().dayOfWeek)

        val weeks = generator.generate(
            yearMonth = oct2026,
            firstDayOfWeek = DayOfWeek.MONDAY,
            today = LocalDate.of(2026, 10, 24)
        )

        assertEquals(5, weeks.size)
        // First row: Mon Sep 28, Tue Sep 29, Wed Sep 30, Thu Oct 1, Fri Oct 2, Sat Oct 3, Sun Oct 4
        val firstRow = weeks.first().days
        assertEquals(LocalDate.of(2026, 9, 28), firstRow[0].date)
        assertFalse(firstRow[0].isCurrentMonth)
        assertEquals(LocalDate.of(2026, 9, 29), firstRow[1].date)
        assertFalse(firstRow[1].isCurrentMonth)
        assertEquals(LocalDate.of(2026, 9, 30), firstRow[2].date)
        assertFalse(firstRow[2].isCurrentMonth)
        assertEquals(LocalDate.of(2026, 10, 1), firstRow[3].date)
        assertTrue(firstRow[3].isCurrentMonth)
        assertEquals(DayOfWeek.THURSDAY, firstRow[3].date.dayOfWeek)

        // Last row ends with Sat Oct 31 and Sun Nov 1
        val lastRow = weeks.last().days
        assertEquals(LocalDate.of(2026, 10, 31), lastRow[5].date)
        assertTrue(lastRow[5].isCurrentMonth)
        assertEquals(LocalDate.of(2026, 11, 1), lastRow[6].date)
        assertFalse(lastRow[6].isCurrentMonth)
    }

    @Test
    fun february2026_has28DaysAndIsNotLeapYear() {
        val feb2026 = YearMonth.of(2026, Month.FEBRUARY)
        assertEquals(28, feb2026.lengthOfMonth())
        assertFalse(feb2026.isLeapYear)

        val weeks = generator.generate(feb2026, DayOfWeek.MONDAY)
        val currentMonthDays = weeks.flatMap { it.days }.filter { it.isCurrentMonth }
        assertEquals(28, currentMonthDays.size)
        assertEquals(LocalDate.of(2026, 2, 28), currentMonthDays.last().date)
    }

    @Test
    fun february2024_has29DaysAndIsLeapYear() {
        val feb2024 = YearMonth.of(2024, Month.FEBRUARY)
        assertEquals(29, feb2024.lengthOfMonth())
        assertTrue(feb2024.isLeapYear)

        val weeks = generator.generate(feb2024, DayOfWeek.MONDAY)
        val currentMonthDays = weeks.flatMap { it.days }.filter { it.isCurrentMonth }
        assertEquals(29, currentMonthDays.size)
        assertEquals(LocalDate.of(2024, 2, 29), currentMonthDays.last().date)
    }

    @Test
    fun january_previousMonthAdjacentDaysAppearCorrectlyFromPreviousYear() {
        val jan2026 = YearMonth.of(2026, Month.JANUARY)
        // Jan 1, 2026 is Thursday; with Monday start, leading days are Dec 29, 30, 31 of 2025
        val weeks = generator.generate(jan2026, DayOfWeek.MONDAY)
        val firstRow = weeks.first().days
        assertEquals(LocalDate.of(2025, 12, 29), firstRow[0].date)
        assertFalse(firstRow[0].isCurrentMonth)
        assertEquals(LocalDate.of(2025, 12, 30), firstRow[1].date)
        assertFalse(firstRow[1].isCurrentMonth)
        assertEquals(LocalDate.of(2025, 12, 31), firstRow[2].date)
        assertFalse(firstRow[2].isCurrentMonth)
        assertEquals(LocalDate.of(2026, 1, 1), firstRow[3].date)
        assertTrue(firstRow[3].isCurrentMonth)
    }

    @Test
    fun december_nextMonthAdjacentDaysCrossIntoNextYear() {
        val dec2026 = YearMonth.of(2026, Month.DECEMBER)
        // Dec 31, 2026 is Thursday; with Monday start, trailing days are Jan 1, 2, 3 of 2027
        val weeks = generator.generate(dec2026, DayOfWeek.MONDAY)
        val lastRow = weeks.last().days
        assertEquals(LocalDate.of(2026, 12, 31), lastRow[3].date)
        assertTrue(lastRow[3].isCurrentMonth)
        assertEquals(LocalDate.of(2027, 1, 1), lastRow[4].date)
        assertFalse(lastRow[4].isCurrentMonth)
        assertEquals(LocalDate.of(2027, 1, 2), lastRow[5].date)
        assertFalse(lastRow[5].isCurrentMonth)
        assertEquals(LocalDate.of(2027, 1, 3), lastRow[6].date)
        assertFalse(lastRow[6].isCurrentMonth)
    }

    @Test
    fun rowCounts_supportsFourFiveAndSixRowMonths() {
        // 4-row month: February 2021 (starts Monday Feb 1, ends Sunday Feb 28)
        val feb2021Weeks = generator.generate(YearMonth.of(2021, Month.FEBRUARY), DayOfWeek.MONDAY)
        assertEquals(4, feb2021Weeks.size)

        // 5-row month: October 2026
        val oct2026Weeks = generator.generate(YearMonth.of(2026, Month.OCTOBER), DayOfWeek.MONDAY)
        assertEquals(5, oct2026Weeks.size)

        // 6-row month: August 2026 (starts Saturday Aug 1, 31 days -> ends Monday Aug 31)
        val aug2026Weeks = generator.generate(YearMonth.of(2026, Month.AUGUST), DayOfWeek.MONDAY)
        assertEquals(6, aug2026Weeks.size)
        assertEquals(LocalDate.of(2026, 8, 31), aug2026Weeks.last().days[0].date)
        assertTrue(aug2026Weeks.last().days[0].isCurrentMonth)
    }

    @Test
    fun weekStartMonday_hasMondayInColumnOne() {
        val ordered = generator.getOrderedWeekdays(DayOfWeek.MONDAY)
        assertEquals(DayOfWeek.MONDAY, ordered.first())
        assertEquals(DayOfWeek.SUNDAY, ordered.last())

        val weeks = generator.generate(YearMonth.of(2026, Month.OCTOBER), DayOfWeek.MONDAY)
        weeks.forEach { week ->
            assertEquals(DayOfWeek.MONDAY, week.days.first().date.dayOfWeek)
            assertEquals(DayOfWeek.SUNDAY, week.days.last().date.dayOfWeek)
        }
    }

    @Test
    fun weekStartSunday_hasSundayInColumnOne() {
        val ordered = generator.getOrderedWeekdays(DayOfWeek.SUNDAY)
        assertEquals(DayOfWeek.SUNDAY, ordered.first())
        assertEquals(DayOfWeek.SATURDAY, ordered.last())

        val weeks = generator.generate(YearMonth.of(2026, Month.OCTOBER), DayOfWeek.SUNDAY)
        weeks.forEach { week ->
            assertEquals(DayOfWeek.SUNDAY, week.days.first().date.dayOfWeek)
            assertEquals(DayOfWeek.SATURDAY, week.days.last().date.dayOfWeek)
        }
    }

    @Test
    fun isoWeek_handlesNewYearWeekYearBoundariesCorrectly() {
        // Dec 29, 2025 is Monday of ISO Week 1 of 2026
        val dec29_2025 = LocalDate.of(2025, 12, 29)
        val detailsDec29 = calculator.computeDateDetails(dec29_2025, Locale.UK)
        assertEquals(1, detailsDec29.isoWeekOfYear)
        assertEquals(2026, detailsDec29.isoWeekYear)

        // Jan 1, 2021 is Friday of ISO Week 53 of 2020
        val jan1_2021 = LocalDate.of(2021, 1, 1)
        val detailsJan1 = calculator.computeDateDetails(jan1_2021, Locale.UK)
        assertEquals(53, detailsJan1.isoWeekOfYear)
        assertEquals(2020, detailsJan1.isoWeekYear)

        // Check week number in generated January 2026 grid with ISO WeekFields
        val jan2026Weeks = generator.generate(
            yearMonth = YearMonth.of(2026, 1),
            firstDayOfWeek = DayOfWeek.MONDAY,
            weekFields = WeekFields.ISO,
            includeWeekNumbers = true
        )
        assertEquals(1, jan2026Weeks.first().weekNumber)
    }

    @Test
    fun todayHighlight_highlightedOnlyWhenVisibleAndDistinctFromSelection() {
        val today = LocalDate.of(2026, 10, 24)
        val selected = LocalDate.of(2026, 10, 15)

        val octWeeks = generator.generate(
            yearMonth = YearMonth.of(2026, 10),
            firstDayOfWeek = DayOfWeek.MONDAY,
            today = today,
            selectedDate = selected
        )
        val octDays = octWeeks.flatMap { it.days }
        val todayCell = octDays.first { it.date == today }
        val selectedCell = octDays.first { it.date == selected }

        assertTrue(todayCell.isToday)
        assertFalse(todayCell.isSelected)
        assertFalse(selectedCell.isToday)
        assertTrue(selectedCell.isSelected)

        // Viewing another month (May 2026) should not highlight today
        val mayWeeks = generator.generate(
            yearMonth = YearMonth.of(2026, 5),
            firstDayOfWeek = DayOfWeek.MONDAY,
            today = today,
            selectedDate = selected
        )
        assertTrue(mayWeeks.flatMap { it.days }.none { it.isToday })
    }

    @Test
    fun selection_updatesSelectedDateDetailsWeekdayDayOfYearAndWeekNumber() {
        val storage = InMemoryMonthGridStorage()
        val repo = MonthGridRepository(storage)
        val vm = MonthGridViewModel(
            repository = repo,
            todayProvider = { LocalDate.of(2026, 10, 1) },
            localeProvider = { Locale.UK }
        )

        val targetDate = LocalDate.of(2026, 10, 24)
        vm.selectDate(targetDate)

        val state = vm.uiState.value
        assertEquals(targetDate, state.selectedDate)

        val details = state.selectedDateDetails
        assertNotNull(details)
        assertEquals(DayOfWeek.SATURDAY, details!!.dayOfWeek)
        assertEquals(24, details.dayOfMonth)
        assertEquals(297, details.dayOfYear)
        assertEquals(365, details.lengthOfYear)
        assertEquals(365 - 297, details.daysRemainingInYear)
        assertEquals(43, details.isoWeekOfYear)
        assertFalse(details.isLeapYear)

        // Verify December 31 has 0 days remaining
        vm.selectDate(LocalDate.of(2026, 12, 31))
        assertEquals(0, vm.uiState.value.selectedDateDetails!!.daysRemainingInYear)
    }

    @Test
    fun monthAndYearNavigation_crossesYearBoundariesAccurately() {
        val storage = InMemoryMonthGridStorage()
        val vm = MonthGridViewModel(
            repository = MonthGridRepository(storage),
            todayProvider = { LocalDate.of(2026, 1, 15) }
        )

        // January 2026 -> previousMonth -> December 2025
        assertEquals(YearMonth.of(2026, 1), vm.uiState.value.currentMonth)
        vm.previousMonth()
        assertEquals(YearMonth.of(2025, 12), vm.uiState.value.currentMonth)

        // December 2025 -> nextMonth -> January 2026
        vm.nextMonth()
        assertEquals(YearMonth.of(2026, 1), vm.uiState.value.currentMonth)

        // Year navigation: 2026 -> 2027 -> 2026
        vm.nextYear()
        assertEquals(YearMonth.of(2027, 1), vm.uiState.value.currentMonth)
        vm.previousYear()
        assertEquals(YearMonth.of(2026, 1), vm.uiState.value.currentMonth)
    }

    @Test
    fun leapYear_2024HasFeb29And2025HasFeb28() {
        val feb2024 = YearMonth.of(2024, 2)
        val feb2025 = YearMonth.of(2025, 2)

        assertTrue(feb2024.isLeapYear)
        assertEquals(29, feb2024.lengthOfMonth())
        assertEquals(LocalDate.of(2024, 2, 29), feb2024.atEndOfMonth())
        assertEquals(366, feb2024.atDay(1).lengthOfYear())

        assertFalse(feb2025.isLeapYear)
        assertEquals(28, feb2025.lengthOfMonth())
        assertEquals(LocalDate.of(2025, 2, 28), feb2025.atEndOfMonth())
        assertEquals(365, feb2025.atDay(1).lengthOfYear())
    }

    @Test
    fun history_save10MonthsThen11thRemovesOldestAndPreventsConsecutiveDuplicates() {
        val storage = InMemoryMonthGridStorage()
        val vm = MonthGridViewModel(
            repository = MonthGridRepository(storage),
            todayProvider = { LocalDate.of(2026, 1, 1) }
        )

        // Save 10 distinct months (Jan 2026 .. Oct 2026)
        for (month in 1..10) {
            vm.selectMonth(YearMonth.of(2026, month))
            vm.saveCurrentMonthToHistory()
        }
        assertEquals(10, vm.uiState.value.history.size)
        assertEquals(10, vm.uiState.value.history.first().month)
        assertEquals(1, vm.uiState.value.history.last().month)

        // Saving the exact same month/date consecutively should not duplicate or evict
        vm.saveCurrentMonthToHistory()
        assertEquals(10, vm.uiState.value.history.size)
        assertEquals(1, vm.uiState.value.history.last().month)

        // Save 11th month (November 2026) -> oldest (January, month 1) is removed
        vm.selectMonth(YearMonth.of(2026, 11))
        vm.saveCurrentMonthToHistory()
        val history = vm.uiState.value.history
        assertEquals(10, history.size)
        assertEquals(11, history.first().month)
        assertEquals(2, history.last().month)
        assertTrue(history.none { it.month == 1 })
    }

    @Test
    fun persistence_restartingViewModelRestoresLastViewedMonthWhenEnabled() {
        val sharedStorage = InMemoryMonthGridStorage()

        val vm1 = MonthGridViewModel(
            repository = MonthGridRepository(sharedStorage),
            todayProvider = { LocalDate.of(2026, 10, 24) }
        )
        vm1.selectMonth(YearMonth.of(2028, 7))
        vm1.selectDate(LocalDate.of(2028, 7, 19))

        // Simulate app restart with restoreLastMonth = true (default)
        val vm2 = MonthGridViewModel(
            repository = MonthGridRepository(sharedStorage),
            todayProvider = { LocalDate.of(2026, 10, 24) }
        )
        assertEquals(YearMonth.of(2028, 7), vm2.uiState.value.currentMonth)
        assertEquals(LocalDate.of(2028, 7, 19), vm2.uiState.value.selectedDate)

        // Disable restoreLastMonth and simulate restart
        vm2.setRestoreLastMonth(false)
        val vm3 = MonthGridViewModel(
            repository = MonthGridRepository(sharedStorage),
            todayProvider = { LocalDate.of(2026, 10, 24) }
        )
        assertEquals(YearMonth.of(2026, 10), vm3.uiState.value.currentMonth)
        assertNull(vm3.uiState.value.selectedDate)
    }

    @Test
    fun accessibilityAndPlainTextFormatting_produceExpectedDescriptions() {
        val oct2026 = YearMonth.of(2026, 10)
        val normalDay = CalendarDay(
            date = LocalDate.of(2026, 10, 24),
            isCurrentMonth = true,
            isToday = false,
            isSelected = false
        )
        assertEquals(
            "Saturday, 24 October 2026.",
            formatter.formatDayAccessibilityDescription(normalDay, oct2026, Locale.UK)
        )

        val prevMonthDay = CalendarDay(
            date = LocalDate.of(2026, 9, 30),
            isCurrentMonth = false,
            isToday = false,
            isSelected = false
        )
        assertEquals(
            "Wednesday, 30 September 2026, previous month.",
            formatter.formatDayAccessibilityDescription(prevMonthDay, oct2026, Locale.UK)
        )

        val todayAndSelectedDay = CalendarDay(
            date = LocalDate.of(2026, 10, 24),
            isCurrentMonth = true,
            isToday = true,
            isSelected = true
        )
        assertEquals(
            "Saturday, 24 October 2026, today and selected.",
            formatter.formatDayAccessibilityDescription(todayAndSelectedDay, oct2026, Locale.UK)
        )
    }
}
