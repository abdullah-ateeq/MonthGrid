package com.example.monthgrid.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.monthgrid.data.SharedPreferencesMonthGridStorage
import com.example.monthgrid.domain.GenerateMonthGridUseCase
import com.example.monthgrid.formatter.CalendarFormatter
import com.example.monthgrid.model.CalendarDensity
import com.example.monthgrid.model.DateFormatOption
import com.example.monthgrid.model.MonthGridHistory
import com.example.monthgrid.model.MonthGridSettings
import com.example.monthgrid.model.ThemeMode
import com.example.monthgrid.model.WeekNumberingMode
import com.example.monthgrid.model.WeekStartOption
import com.example.monthgrid.repository.MonthGridRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

class MonthGridViewModel(
    private val repository: MonthGridRepository = MonthGridRepository(),
    private val generateMonthGridUseCase: GenerateMonthGridUseCase = GenerateMonthGridUseCase(),
    private val formatter: CalendarFormatter = CalendarFormatter(),
    private val todayProvider: () -> LocalDate = { LocalDate.now() },
    private val localeProvider: () -> Locale = { Locale.getDefault() }
) : ViewModel() {

    private val _uiState = MutableStateFlow(createInitialState())
    val uiState: StateFlow<MonthGridUiState> = _uiState.asStateFlow()

    private fun createInitialState(): MonthGridUiState {
        val settings = repository.getSettings()
        val today = todayProvider()
        val initialMonth = repository.resolveInitialMonth(settings, YearMonth.from(today))
        val initialSelectedDate = repository.resolveInitialSelectedDate(settings)
        val history = repository.getHistory()

        return buildState(
            yearMonth = initialMonth,
            selectedDate = initialSelectedDate,
            settings = settings,
            history = history
        )
    }

    private fun buildState(
        yearMonth: YearMonth,
        selectedDate: LocalDate?,
        settings: MonthGridSettings,
        history: List<MonthGridHistory>,
        errorMessage: String? = null,
        statusMessage: String? = null
    ): MonthGridUiState {
        val today = todayProvider()
        val locale = localeProvider()
        val bundle = generateMonthGridUseCase.execute(
            yearMonth = yearMonth,
            selectedDate = selectedDate,
            settings = settings,
            today = today,
            locale = locale
        )

        return MonthGridUiState(
            currentMonth = yearMonth,
            selectedDate = selectedDate,
            calendarWeeks = bundle.weeks,
            monthDays = yearMonth.lengthOfMonth(),
            monthStartDay = yearMonth.atDay(1).dayOfWeek,
            monthEndDay = yearMonth.atEndOfMonth().dayOfWeek,
            history = history,
            errorMessage = errorMessage,
            settings = settings,
            orderedWeekdays = bundle.orderedWeekdays,
            monthDetails = bundle.monthDetails,
            selectedDateDetails = bundle.selectedDateDetails,
            selectedWeek = bundle.selectedWeek,
            statusMessage = statusMessage
        )
    }

    private fun updateCalendarState(
        yearMonth: YearMonth = _uiState.value.currentMonth,
        selectedDate: LocalDate? = _uiState.value.selectedDate,
        settings: MonthGridSettings = _uiState.value.settings,
        history: List<MonthGridHistory> = _uiState.value.history,
        errorMessage: String? = null,
        statusMessage: String? = null,
        persistMonthAndDate: Boolean = true
    ) {
        if (persistMonthAndDate) {
            repository.saveLastViewedState(yearMonth, selectedDate)
        }
        _uiState.value = buildState(
            yearMonth = yearMonth,
            selectedDate = selectedDate,
            settings = settings,
            history = history,
            errorMessage = errorMessage,
            statusMessage = statusMessage
        )
    }

    fun previousMonth() {
        val next = _uiState.value.currentMonth.minusMonths(1)
        updateCalendarState(yearMonth = next)
    }

    fun nextMonth() {
        val next = _uiState.value.currentMonth.plusMonths(1)
        updateCalendarState(yearMonth = next)
    }

    fun previousYear() {
        val next = _uiState.value.currentMonth.minusYears(1)
        updateCalendarState(yearMonth = next)
    }

    fun nextYear() {
        val next = _uiState.value.currentMonth.plusYears(1)
        updateCalendarState(yearMonth = next)
    }

    fun selectMonth(yearMonth: YearMonth) {
        updateCalendarState(yearMonth = yearMonth)
    }

    fun selectMonthAndYear(year: Int, month: Int) {
        if (month !in 1..12) {
            updateCalendarState(errorMessage = "Invalid month: $month")
            return
        }
        val target = YearMonth.of(year, month)
        updateCalendarState(yearMonth = target)
    }

    fun selectDate(date: LocalDate) {
        updateCalendarState(selectedDate = date)
    }

    fun jumpToDate(date: LocalDate) {
        val targetMonth = YearMonth.from(date)
        updateCalendarState(
            yearMonth = targetMonth,
            selectedDate = date
        )
    }

    fun jumpToToday() {
        val today = todayProvider()
        val currentMonth = YearMonth.from(today)
        updateCalendarState(
            yearMonth = currentMonth,
            selectedDate = today
        )
    }

    fun clearSelectedDate() {
        updateCalendarState(selectedDate = null)
    }

    fun saveCurrentMonthToHistory() {
        val current = _uiState.value
        val updatedHistory = repository.addHistory(
            yearMonth = current.currentMonth,
            selectedDate = current.selectedDate
        )
        val monthTitle = formatter.formatMonthYear(current.currentMonth, localeProvider())
        updateCalendarState(
            history = updatedHistory,
            statusMessage = "Saved $monthTitle to history",
            persistMonthAndDate = false
        )
    }

    fun restoreFromHistory(item: MonthGridHistory) {
        val targetMonth = YearMonth.of(item.year, item.month)
        val restoredDate = item.parsedSelectedDate
        updateCalendarState(
            yearMonth = targetMonth,
            selectedDate = restoredDate,
            statusMessage = "Restored ${formatter.formatMonthYear(targetMonth, localeProvider())}"
        )
    }

    fun deleteHistoryItem(id: String) {
        val updated = repository.deleteHistoryItem(id)
        updateCalendarState(
            history = updated,
            persistMonthAndDate = false
        )
    }

    fun clearAllHistory() {
        val updated = repository.clearHistory()
        updateCalendarState(
            history = updated,
            statusMessage = "History cleared",
            persistMonthAndDate = false
        )
    }

    fun getPlainTextCalendar(): String {
        val state = _uiState.value
        return formatter.formatPlainTextCalendar(
            yearMonth = state.currentMonth,
            orderedWeekdays = state.orderedWeekdays,
            weeks = state.calendarWeeks,
            showWeekNumbers = state.settings.showWeekNumbers,
            locale = localeProvider()
        )
    }

    fun getFormattedSelectedDate(): String? {
        val state = _uiState.value
        val date = state.selectedDate ?: return null
        return formatter.formatFullDateWithWeekday(
            date = date,
            formatOption = state.settings.dateFormat,
            locale = localeProvider()
        )
    }

    fun resetToDefaults() {
        val defaultSettings = repository.resetSettings()
        val nowMonth = YearMonth.from(todayProvider())
        updateCalendarState(
            yearMonth = nowMonth,
            selectedDate = null,
            settings = defaultSettings,
            statusMessage = "Reset to current month and default settings"
        )
    }

    fun resetSettingsOnly() {
        val defaultSettings = repository.resetSettings()
        updateCalendarState(
            settings = defaultSettings,
            statusMessage = "Settings restored to defaults",
            persistMonthAndDate = false
        )
    }

    fun updateSettings(transform: (MonthGridSettings) -> MonthGridSettings) {
        val updated = transform(_uiState.value.settings)
        repository.updateSettings(updated)
        updateCalendarState(
            settings = updated,
            persistMonthAndDate = false
        )
    }

    fun setThemeMode(mode: ThemeMode) = updateSettings { it.copy(themeMode = mode) }
    fun setDynamicColor(enabled: Boolean) = updateSettings { it.copy(dynamicColor = enabled) }
    fun setWeekStart(option: WeekStartOption) = updateSettings { it.copy(weekStart = option) }
    fun setWeekNumbering(mode: WeekNumberingMode) = updateSettings { it.copy(weekNumbering = mode) }
    fun setShowWeekNumbers(enabled: Boolean) = updateSettings { it.copy(showWeekNumbers = enabled) }
    fun setShowAdjacentMonthDays(enabled: Boolean) = updateSettings { it.copy(showAdjacentMonthDays = enabled) }
    fun setCalendarDensity(density: CalendarDensity) = updateSettings { it.copy(calendarDensity = density) }
    fun setShowDayDetails(enabled: Boolean) = updateSettings { it.copy(showDayDetails = enabled) }
    fun setShowWeekView(enabled: Boolean) = updateSettings { it.copy(showWeekView = enabled) }
    fun setShowMonthProgress(enabled: Boolean) = updateSettings { it.copy(showMonthProgress = enabled) }
    fun setShowLeapYear(enabled: Boolean) = updateSettings { it.copy(showLeapYear = enabled) }
    fun setShowDayOfYear(enabled: Boolean) = updateSettings { it.copy(showDayOfYear = enabled) }
    fun setShowIsoWeek(enabled: Boolean) = updateSettings { it.copy(showIsoWeek = enabled) }
    fun setDateFormat(format: DateFormatOption) = updateSettings { it.copy(dateFormat = format) }
    fun setHapticFeedback(enabled: Boolean) = updateSettings { it.copy(hapticFeedback = enabled) }
    fun setAnimations(enabled: Boolean) = updateSettings { it.copy(animations = enabled) }
    fun setConfirmDelete(enabled: Boolean) = updateSettings { it.copy(confirmDelete = enabled) }
    fun setRestoreLastMonth(enabled: Boolean) = updateSettings { it.copy(restoreLastMonth = enabled) }

    fun clearStatusMessage() {
        _uiState.value = _uiState.value.copy(statusMessage = null, errorMessage = null)
    }

    companion object {
        fun provideFactory(context: Context): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    val storage = SharedPreferencesMonthGridStorage.create(context)
                    val repository = MonthGridRepository(storage)
                    return MonthGridViewModel(repository = repository) as T
                }
            }
        }
    }
}
