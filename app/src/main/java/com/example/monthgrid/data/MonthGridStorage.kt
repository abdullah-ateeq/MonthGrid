package com.example.monthgrid.data

import android.content.Context
import android.content.SharedPreferences
import com.example.monthgrid.history.HistoryManager
import com.example.monthgrid.model.CalendarDensity
import com.example.monthgrid.model.DateFormatOption
import com.example.monthgrid.model.MonthGridHistory
import com.example.monthgrid.model.MonthGridSettings
import com.example.monthgrid.model.ThemeMode
import com.example.monthgrid.model.WeekNumberingMode
import com.example.monthgrid.model.WeekStartOption
import java.time.LocalDate
import java.time.YearMonth

interface MonthGridStorage {
    fun loadSettings(): MonthGridSettings
    fun saveSettings(settings: MonthGridSettings)
    fun resetSettings(): MonthGridSettings

    fun loadLastViewedMonth(): YearMonth?
    fun saveLastViewedMonth(yearMonth: YearMonth)

    fun loadSelectedDate(): LocalDate?
    fun saveSelectedDate(date: LocalDate?)

    fun loadHistory(): List<MonthGridHistory>
    fun saveHistory(history: List<MonthGridHistory>)
}

class SharedPreferencesMonthGridStorage(
    private val prefs: SharedPreferences,
    private val historyManager: HistoryManager = HistoryManager()
) : MonthGridStorage {

    companion object {
        const val PREFS_NAME = "month_grid_prefs"

        private const val KEY_THEME_MODE = "theme_mode"
        private const val KEY_DYNAMIC_COLOR = "dynamic_color"
        private const val KEY_WEEK_START = "week_start"
        private const val KEY_WEEK_NUMBERING = "week_numbering"
        private const val KEY_SHOW_WEEK_NUMBERS = "show_week_numbers"
        private const val KEY_SHOW_ADJACENT_DAYS = "show_adjacent_days"
        private const val KEY_CALENDAR_DENSITY = "calendar_density"
        private const val KEY_SHOW_DAY_DETAILS = "show_day_details"
        private const val KEY_SHOW_WEEK_VIEW = "show_week_view"
        private const val KEY_SHOW_MONTH_PROGRESS = "show_month_progress"
        private const val KEY_SHOW_LEAP_YEAR = "show_leap_year"
        private const val KEY_SHOW_DAY_OF_YEAR = "show_day_of_year"
        private const val KEY_SHOW_ISO_WEEK = "show_iso_week"
        private const val KEY_DATE_FORMAT = "date_format"
        private const val KEY_HAPTIC_FEEDBACK = "haptic_feedback"
        private const val KEY_ANIMATIONS = "animations"
        private const val KEY_CONFIRM_DELETE = "confirm_delete"
        private const val KEY_RESTORE_LAST_MONTH = "restore_last_month"

        private const val KEY_LAST_VIEWED_MONTH = "last_viewed_month"
        private const val KEY_SELECTED_DATE = "selected_date"
        private const val KEY_HISTORY = "history_entries"

        fun create(context: Context): SharedPreferencesMonthGridStorage {
            val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            return SharedPreferencesMonthGridStorage(prefs)
        }
    }

    override fun loadSettings(): MonthGridSettings {
        val defaults = MonthGridSettings()
        return MonthGridSettings(
            themeMode = enumOrDefault(prefs.getString(KEY_THEME_MODE, null), defaults.themeMode),
            dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, defaults.dynamicColor),
            weekStart = enumOrDefault(prefs.getString(KEY_WEEK_START, null), defaults.weekStart),
            weekNumbering = enumOrDefault(prefs.getString(KEY_WEEK_NUMBERING, null), defaults.weekNumbering),
            showWeekNumbers = prefs.getBoolean(KEY_SHOW_WEEK_NUMBERS, defaults.showWeekNumbers),
            showAdjacentMonthDays = prefs.getBoolean(KEY_SHOW_ADJACENT_DAYS, defaults.showAdjacentMonthDays),
            calendarDensity = enumOrDefault(prefs.getString(KEY_CALENDAR_DENSITY, null), defaults.calendarDensity),
            showDayDetails = prefs.getBoolean(KEY_SHOW_DAY_DETAILS, defaults.showDayDetails),
            showWeekView = prefs.getBoolean(KEY_SHOW_WEEK_VIEW, defaults.showWeekView),
            showMonthProgress = prefs.getBoolean(KEY_SHOW_MONTH_PROGRESS, defaults.showMonthProgress),
            showLeapYear = prefs.getBoolean(KEY_SHOW_LEAP_YEAR, defaults.showLeapYear),
            showDayOfYear = prefs.getBoolean(KEY_SHOW_DAY_OF_YEAR, defaults.showDayOfYear),
            showIsoWeek = prefs.getBoolean(KEY_SHOW_ISO_WEEK, defaults.showIsoWeek),
            dateFormat = enumOrDefault(prefs.getString(KEY_DATE_FORMAT, null), defaults.dateFormat),
            hapticFeedback = prefs.getBoolean(KEY_HAPTIC_FEEDBACK, defaults.hapticFeedback),
            animations = prefs.getBoolean(KEY_ANIMATIONS, defaults.animations),
            confirmDelete = prefs.getBoolean(KEY_CONFIRM_DELETE, defaults.confirmDelete),
            restoreLastMonth = prefs.getBoolean(KEY_RESTORE_LAST_MONTH, defaults.restoreLastMonth)
        )
    }

    override fun saveSettings(settings: MonthGridSettings) {
        prefs.edit()
            .putString(KEY_THEME_MODE, settings.themeMode.name)
            .putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
            .putString(KEY_WEEK_START, settings.weekStart.name)
            .putString(KEY_WEEK_NUMBERING, settings.weekNumbering.name)
            .putBoolean(KEY_SHOW_WEEK_NUMBERS, settings.showWeekNumbers)
            .putBoolean(KEY_SHOW_ADJACENT_DAYS, settings.showAdjacentMonthDays)
            .putString(KEY_CALENDAR_DENSITY, settings.calendarDensity.name)
            .putBoolean(KEY_SHOW_DAY_DETAILS, settings.showDayDetails)
            .putBoolean(KEY_SHOW_WEEK_VIEW, settings.showWeekView)
            .putBoolean(KEY_SHOW_MONTH_PROGRESS, settings.showMonthProgress)
            .putBoolean(KEY_SHOW_LEAP_YEAR, settings.showLeapYear)
            .putBoolean(KEY_SHOW_DAY_OF_YEAR, settings.showDayOfYear)
            .putBoolean(KEY_SHOW_ISO_WEEK, settings.showIsoWeek)
            .putString(KEY_DATE_FORMAT, settings.dateFormat.name)
            .putBoolean(KEY_HAPTIC_FEEDBACK, settings.hapticFeedback)
            .putBoolean(KEY_ANIMATIONS, settings.animations)
            .putBoolean(KEY_CONFIRM_DELETE, settings.confirmDelete)
            .putBoolean(KEY_RESTORE_LAST_MONTH, settings.restoreLastMonth)
            .apply()
    }

    override fun resetSettings(): MonthGridSettings {
        val defaults = MonthGridSettings()
        saveSettings(defaults)
        return defaults
    }

    override fun loadLastViewedMonth(): YearMonth? {
        val raw = prefs.getString(KEY_LAST_VIEWED_MONTH, null) ?: return null
        return runCatching { YearMonth.parse(raw) }.getOrNull()
    }

    override fun saveLastViewedMonth(yearMonth: YearMonth) {
        prefs.edit().putString(KEY_LAST_VIEWED_MONTH, yearMonth.toString()).apply()
    }

    override fun loadSelectedDate(): LocalDate? {
        val raw = prefs.getString(KEY_SELECTED_DATE, null) ?: return null
        if (raw.isBlank()) return null
        return runCatching { LocalDate.parse(raw) }.getOrNull()
    }

    override fun saveSelectedDate(date: LocalDate?) {
        prefs.edit().putString(KEY_SELECTED_DATE, date?.toString()).apply()
    }

    override fun loadHistory(): List<MonthGridHistory> {
        val raw = prefs.getString(KEY_HISTORY, null)
        return historyManager.deserialize(raw)
    }

    override fun saveHistory(history: List<MonthGridHistory>) {
        prefs.edit().putString(KEY_HISTORY, historyManager.serialize(history)).apply()
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(value: String?, default: T): T {
        if (value == null) return default
        return runCatching { enumValueOf<T>(value) }.getOrDefault(default)
    }
}

class InMemoryMonthGridStorage(
    initialSettings: MonthGridSettings = MonthGridSettings(),
    initialLastViewedMonth: YearMonth? = null,
    initialSelectedDate: LocalDate? = null,
    initialHistory: List<MonthGridHistory> = emptyList()
) : MonthGridStorage {
    private var settings: MonthGridSettings = initialSettings
    private var lastViewedMonth: YearMonth? = initialLastViewedMonth
    private var selectedDate: LocalDate? = initialSelectedDate
    private var history: List<MonthGridHistory> = initialHistory

    override fun loadSettings(): MonthGridSettings = settings

    override fun saveSettings(settings: MonthGridSettings) {
        this.settings = settings
    }

    override fun resetSettings(): MonthGridSettings {
        settings = MonthGridSettings()
        return settings
    }

    override fun loadLastViewedMonth(): YearMonth? = lastViewedMonth

    override fun saveLastViewedMonth(yearMonth: YearMonth) {
        this.lastViewedMonth = yearMonth
    }

    override fun loadSelectedDate(): LocalDate? = selectedDate

    override fun saveSelectedDate(date: LocalDate?) {
        this.selectedDate = date
    }

    override fun loadHistory(): List<MonthGridHistory> = history

    override fun saveHistory(history: List<MonthGridHistory>) {
        this.history = history
    }
}
