package com.example.monthgrid.repository

import com.example.monthgrid.data.InMemoryMonthGridStorage
import com.example.monthgrid.data.MonthGridStorage
import com.example.monthgrid.history.HistoryManager
import com.example.monthgrid.model.MonthGridHistory
import com.example.monthgrid.model.MonthGridSettings
import java.time.LocalDate
import java.time.YearMonth

class MonthGridRepository(
    private val storage: MonthGridStorage = InMemoryMonthGridStorage(),
    private val historyManager: HistoryManager = HistoryManager()
) {
    fun getSettings(): MonthGridSettings = storage.loadSettings()

    fun updateSettings(settings: MonthGridSettings) {
        storage.saveSettings(settings)
    }

    fun resetSettings(): MonthGridSettings {
        return storage.resetSettings()
    }

    fun resolveInitialMonth(
        settings: MonthGridSettings,
        fallbackNow: YearMonth = YearMonth.now()
    ): YearMonth {
        return if (settings.restoreLastMonth) {
            storage.loadLastViewedMonth() ?: fallbackNow
        } else {
            fallbackNow
        }
    }

    fun resolveInitialSelectedDate(settings: MonthGridSettings): LocalDate? {
        return if (settings.restoreLastMonth) {
            storage.loadSelectedDate()
        } else {
            null
        }
    }

    fun saveLastViewedState(yearMonth: YearMonth, selectedDate: LocalDate?) {
        storage.saveLastViewedMonth(yearMonth)
        storage.saveSelectedDate(selectedDate)
    }

    fun getHistory(): List<MonthGridHistory> = storage.loadHistory()

    fun addHistory(
        yearMonth: YearMonth,
        selectedDate: LocalDate?,
        savedAtMillis: Long = System.currentTimeMillis()
    ): List<MonthGridHistory> {
        val current = storage.loadHistory()
        val updated = historyManager.addHistoryEntry(
            currentHistory = current,
            yearMonth = yearMonth,
            selectedDate = selectedDate,
            savedAtMillis = savedAtMillis
        )
        storage.saveHistory(updated)
        return updated
    }

    fun deleteHistoryItem(id: String): List<MonthGridHistory> {
        val current = storage.loadHistory()
        val updated = historyManager.removeHistoryEntry(current, id)
        storage.saveHistory(updated)
        return updated
    }

    fun clearHistory(): List<MonthGridHistory> {
        storage.saveHistory(emptyList())
        return emptyList()
    }
}
