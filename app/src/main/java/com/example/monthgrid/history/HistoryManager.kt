package com.example.monthgrid.history

import com.example.monthgrid.model.MonthGridHistory
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID

class HistoryManager {

    companion object {
        const val MAX_HISTORY_ENTRIES = 10
        private const val ENTRY_SEPARATOR = "\n"
        private const val FIELD_SEPARATOR = "|"
    }

    fun addHistoryEntry(
        currentHistory: List<MonthGridHistory>,
        yearMonth: YearMonth,
        selectedDate: LocalDate?,
        savedAtMillis: Long = System.currentTimeMillis(),
        id: String = UUID.randomUUID().toString()
    ): List<MonthGridHistory> {
        val isoSelectedDate = selectedDate?.toString()
        val latest = currentHistory.firstOrNull()

        // Avoid consecutive duplicate of the same month/year and selected date
        if (latest != null &&
            latest.year == yearMonth.year &&
            latest.month == yearMonth.monthValue &&
            latest.selectedDate == isoSelectedDate
        ) {
            val updatedLatest = latest.copy(savedAt = savedAtMillis)
            return listOf(updatedLatest) + currentHistory.drop(1)
        }

        val newEntry = MonthGridHistory(
            id = id,
            year = yearMonth.year,
            month = yearMonth.monthValue,
            selectedDate = isoSelectedDate,
            savedAt = savedAtMillis
        )

        return (listOf(newEntry) + currentHistory).take(MAX_HISTORY_ENTRIES)
    }

    fun removeHistoryEntry(
        currentHistory: List<MonthGridHistory>,
        id: String
    ): List<MonthGridHistory> {
        return currentHistory.filterNot { it.id == id }
    }

    fun serialize(history: List<MonthGridHistory>): String {
        return history.joinToString(ENTRY_SEPARATOR) { item ->
            listOf(
                item.id.replace(FIELD_SEPARATOR, "_"),
                item.year.toString(),
                item.month.toString(),
                item.selectedDate ?: "",
                item.savedAt.toString()
            ).joinToString(FIELD_SEPARATOR)
        }
    }

    fun deserialize(raw: String?): List<MonthGridHistory> {
        if (raw.isNullOrBlank()) return emptyList()
        return raw.split(ENTRY_SEPARATOR)
            .mapNotNull { line ->
                val parts = line.split(FIELD_SEPARATOR)
                if (parts.size < 5) return@mapNotNull null
                val id = parts[0].takeIf { it.isNotBlank() } ?: return@mapNotNull null
                val year = parts[1].toIntOrNull() ?: return@mapNotNull null
                val month = parts[2].toIntOrNull()?.takeIf { it in 1..12 } ?: return@mapNotNull null
                val selectedDate = parts[3].takeIf { it.isNotBlank() }
                val savedAt = parts[4].toLongOrNull() ?: 0L
                MonthGridHistory(
                    id = id,
                    year = year,
                    month = month,
                    selectedDate = selectedDate,
                    savedAt = savedAt
                )
            }
            .take(MAX_HISTORY_ENTRIES)
    }
}
