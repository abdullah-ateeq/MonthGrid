package com.example.monthgrid.model

import java.time.LocalDate

data class CalendarDay(
    val date: LocalDate,
    val isCurrentMonth: Boolean,
    val isToday: Boolean,
    val isSelected: Boolean
)
