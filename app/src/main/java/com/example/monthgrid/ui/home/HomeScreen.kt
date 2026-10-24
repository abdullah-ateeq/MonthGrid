package com.example.monthgrid.ui.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monthgrid.formatter.CalendarFormatter
import com.example.monthgrid.model.CalendarDay
import com.example.monthgrid.model.CalendarDensity
import com.example.monthgrid.model.CalendarWeek
import com.example.monthgrid.model.DateDetails
import com.example.monthgrid.model.DateFormatOption
import com.example.monthgrid.ui.MonthGridUiState
import com.example.monthgrid.util.SystemActionHelper
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    uiState: MonthGridUiState,
    formatter: CalendarFormatter,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onPreviousYear: () -> Unit,
    onNextYear: () -> Unit,
    onSelectMonthYear: (YearMonth) -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onJumpToDate: (LocalDate) -> Unit,
    onTodayClick: () -> Unit,
    onSaveHistory: () -> Unit,
    onReset: () -> Unit,
    onCopyCalendar: () -> String,
    onCopySelectedDate: () -> String?,
    onShowFeedback: (String) -> Unit
) {
    val context = LocalContext.current
    val view = LocalView.current
    val locale = Locale.getDefault()
    val hapticsEnabled = uiState.settings.hapticFeedback
    val animationsEnabled = uiState.settings.animations

    var showMonthYearPicker by rememberSaveable { mutableStateOf(false) }
    var showJumpDatePicker by rememberSaveable { mutableStateOf(false) }
    var monthDetailsExpanded by rememberSaveable { mutableStateOf(false) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // App Identity Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MonthGrid",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "A compact calendar for any month.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Secondary option: Jump to Date (Material 3 DatePicker)
            OutlinedButton(
                onClick = {
                    SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                    showJumpDatePicker = true
                },
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Jump to Date",
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        // Main Calendar Card (Hero Element)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 14.dp)
            ) {
                // Year Navigation Bar: ‹ 2025   2026   2027 ›
                YearNavigationRow(
                    currentYear = uiState.currentMonth.year,
                    onPreviousYear = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        onPreviousYear()
                    },
                    onNextYear = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        onNextYear()
                    },
                    onYearClick = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        showMonthYearPicker = true
                    }
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Month Header: ‹   October 2026   ›
                MonthHeaderRow(
                    monthTitle = formatter.formatMonthYear(uiState.currentMonth, locale),
                    onPreviousMonth = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        onPreviousMonth()
                    },
                    onNextMonth = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        onNextMonth()
                    },
                    onMonthTitleClick = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        showMonthYearPicker = true
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Weekday Header Row
                WeekdayHeaderRow(
                    orderedWeekdays = uiState.orderedWeekdays,
                    showWeekNumbers = uiState.settings.showWeekNumbers,
                    formatter = formatter,
                    locale = locale
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 7-Column Calendar Grid with subtle horizontal transition when changing months
                if (animationsEnabled) {
                    AnimatedContent(
                        targetState = uiState.currentMonth,
                        transitionSpec = {
                            val forward = targetState.isAfter(initialState)
                            val enter = slideInHorizontally(
                                animationSpec = tween(240),
                                initialOffsetX = { fullWidth -> if (forward) fullWidth / 4 else -fullWidth / 4 }
                            ) + fadeIn(animationSpec = tween(240))
                            val exit = slideOutHorizontally(
                                animationSpec = tween(240),
                                targetOffsetX = { fullWidth -> if (forward) -fullWidth / 4 else fullWidth / 4 }
                            ) + fadeOut(animationSpec = tween(200))
                            (enter togetherWith exit).using(SizeTransform(clip = false))
                        },
                        label = "MonthGridTransition"
                    ) { displayedMonth ->
                        // Use weeks matching current state
                        CalendarWeeksGrid(
                            displayedMonth = displayedMonth,
                            weeks = uiState.calendarWeeks,
                            showWeekNumbers = uiState.settings.showWeekNumbers,
                            showAdjacentMonthDays = uiState.settings.showAdjacentMonthDays,
                            density = uiState.settings.calendarDensity,
                            formatter = formatter,
                            locale = locale,
                            onDayClick = { day ->
                                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                                onSelectDate(day.date)
                            }
                        )
                    }
                } else {
                    CalendarWeeksGrid(
                        displayedMonth = uiState.currentMonth,
                        weeks = uiState.calendarWeeks,
                        showWeekNumbers = uiState.settings.showWeekNumbers,
                        showAdjacentMonthDays = uiState.settings.showAdjacentMonthDays,
                        density = uiState.settings.calendarDensity,
                        formatter = formatter,
                        locale = locale,
                        onDayClick = { day ->
                            SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                            onSelectDate(day.date)
                        }
                    )
                }

                // Optional Month Progress Bar
                val progressDay = remember(uiState.selectedDate, uiState.currentMonth) {
                    val selected = uiState.selectedDate
                    if (selected != null && YearMonth.from(selected) == uiState.currentMonth) {
                        selected.dayOfMonth
                    } else {
                        val now = LocalDate.now()
                        if (YearMonth.from(now) == uiState.currentMonth) now.dayOfMonth else null
                    }
                }

                AnimatedVisibility(
                    visible = uiState.settings.showMonthProgress && progressDay != null,
                    enter = expandVertically(tween(220)) + fadeIn(tween(220)),
                    exit = shrinkVertically(tween(200)) + fadeOut(tween(200))
                ) {
                    if (progressDay != null) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp, start = 4.dp, end = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = formatter.formatMonthName(uiState.currentMonth, locale),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "Day $progressDay of ${uiState.monthDays}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.SemiBold
                                    ),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = {
                                    (progressDay.toFloat() / uiState.monthDays.toFloat()).coerceIn(0f, 1f)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = MaterialTheme.colorScheme.primary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Primary Quick Controls Bar: TODAY, COPY CALENDAR, SAVE, SHARE, RESET
        QuickActionsPanel(
            hasSelectedDate = uiState.selectedDate != null,
            onTodayClick = {
                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                onTodayClick()
            },
            onCopyCalendarClick = {
                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                val text = onCopyCalendar()
                SystemActionHelper.copyToClipboard(
                    context = context,
                    label = "MonthGrid Calendar",
                    text = text
                )
                onShowFeedback("Copied ${formatter.formatMonthYear(uiState.currentMonth, locale)} calendar")
            },
            onCopyDateClick = {
                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                val dateText = onCopySelectedDate()
                if (dateText != null) {
                    SystemActionHelper.copyToClipboard(
                        context = context,
                        label = "Selected Date",
                        text = dateText
                    )
                    onShowFeedback("Copied $dateText")
                }
            },
            onSaveClick = {
                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                onSaveHistory()
            },
            onShareClick = {
                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                val text = onCopyCalendar()
                val title = formatter.formatMonthYear(uiState.currentMonth, locale)
                SystemActionHelper.shareCalendarText(
                    context = context,
                    subject = "MonthGrid - $title",
                    calendarText = text
                )
            },
            onResetClick = {
                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                onReset()
            }
        )

        // Selected Date & Date Details Panel
        val dateDetails = uiState.selectedDateDetails
        AnimatedVisibility(
            visible = dateDetails != null && uiState.settings.showDayDetails,
            enter = expandVertically(tween(if (animationsEnabled) 250 else 0)) +
                fadeIn(tween(if (animationsEnabled) 250 else 0)),
            exit = shrinkVertically(tween(if (animationsEnabled) 200 else 0)) +
                fadeOut(tween(if (animationsEnabled) 200 else 0))
        ) {
            if (dateDetails != null) {
                SelectedDateDetailsCard(
                    details = dateDetails,
                    selectedWeek = uiState.selectedWeek,
                    orderedWeekdays = uiState.orderedWeekdays,
                    dateFormat = uiState.settings.dateFormat,
                    showWeekView = uiState.settings.showWeekView,
                    showDayOfYear = uiState.settings.showDayOfYear,
                    showLeapYear = uiState.settings.showLeapYear,
                    showIsoWeek = uiState.settings.showIsoWeek,
                    formatter = formatter,
                    locale = locale,
                    onSelectDayFromWeek = { date ->
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        onSelectDate(date)
                    },
                    onCopyDate = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        val text = onCopySelectedDate()
                        if (text != null) {
                            SystemActionHelper.copyToClipboard(context, "Selected Date", text)
                            onShowFeedback("Copied $text")
                        }
                    }
                )
            }
        }

        // Month Summary & Expandable Month Details Card
        MonthSummaryAndDetailsCard(
            uiState = uiState,
            expanded = monthDetailsExpanded,
            animationsEnabled = animationsEnabled,
            formatter = formatter,
            locale = locale,
            onToggleExpand = {
                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                monthDetailsExpanded = !monthDetailsExpanded
            }
        )

        Spacer(modifier = Modifier.height(12.dp))
    }

    if (showMonthYearPicker) {
        MonthYearPickerDialog(
            initialYearMonth = uiState.currentMonth,
            locale = locale,
            onDismiss = { showMonthYearPicker = false },
            onMonthYearSelected = { pickedYearMonth ->
                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                showMonthYearPicker = false
                onSelectMonthYear(pickedYearMonth)
            }
        )
    }

    if (showJumpDatePicker) {
        JumpToDateDialog(
            initialDate = uiState.selectedDate ?: uiState.currentMonth.atDay(1),
            onDismiss = { showJumpDatePicker = false },
            onDateSelected = { pickedDate ->
                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                showJumpDatePicker = false
                onJumpToDate(pickedDate)
            }
        )
    }
}

@Composable
private fun YearNavigationRow(
    currentYear: Int,
    onPreviousYear: () -> Unit,
    onNextYear: () -> Unit,
    onYearClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onPreviousYear,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.semantics { contentDescription = "Previous year." }
        ) {
            Text(
                text = "‹ ${currentYear - 1}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = currentYear.toString(),
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .clickable(onClick = onYearClick)
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )

        TextButton(
            onClick = onNextYear,
            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
            modifier = Modifier.semantics { contentDescription = "Next year." }
        ) {
            Text(
                text = "${currentYear + 1} ›",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun MonthHeaderRow(
    monthTitle: String,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onMonthTitleClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier.semantics {
                contentDescription = "Previous month."
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "Previous month.",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(
                    role = Role.Button,
                    onClickLabel = "Select month and year",
                    onClick = onMonthTitleClick
                )
                .padding(horizontal = 16.dp, vertical = 6.dp)
        ) {
            Text(
                text = monthTitle,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
        }

        IconButton(
            onClick = onNextMonth,
            modifier = Modifier.semantics {
                contentDescription = "Next month."
            }
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = "Next month.",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun WeekdayHeaderRow(
    orderedWeekdays: List<DayOfWeek>,
    showWeekNumbers: Boolean,
    formatter: CalendarFormatter,
    locale: Locale
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showWeekNumbers) {
            Box(
                modifier = Modifier.width(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "W",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center
                )
            }
        }

        orderedWeekdays.forEach { dayOfWeek ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = formatter.formatWeekdayShort(dayOfWeek, locale, uppercase = true),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.4.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CalendarWeeksGrid(
    displayedMonth: YearMonth,
    weeks: List<CalendarWeek>,
    showWeekNumbers: Boolean,
    showAdjacentMonthDays: Boolean,
    density: CalendarDensity,
    formatter: CalendarFormatter,
    locale: Locale,
    onDayClick: (CalendarDay) -> Unit
) {
    val minCellHeight = when (density) {
        CalendarDensity.COMPACT -> 44.dp
        CalendarDensity.COMFORTABLE -> 52.dp
    }
    val verticalRowSpacing = when (density) {
        CalendarDensity.COMPACT -> 4.dp
        CalendarDensity.COMFORTABLE -> 8.dp
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(verticalRowSpacing)
    ) {
        weeks.forEach { week ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (showWeekNumbers) {
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .defaultMinSize(minHeight = minCellHeight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = week.weekNumber?.toString() ?: "",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Medium
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                week.days.forEach { day ->
                    val shouldDisplay = day.isCurrentMonth || showAdjacentMonthDays
                    if (shouldDisplay) {
                        CalendarDayCell(
                            day = day,
                            displayedMonth = displayedMonth,
                            minCellHeight = minCellHeight,
                            formatter = formatter,
                            locale = locale,
                            modifier = Modifier.weight(1f),
                            onClick = { onDayClick(day) }
                        )
                    } else {
                        // Empty cell when adjacent-month days are disabled
                        Spacer(
                            modifier = Modifier
                                .weight(1f)
                                .defaultMinSize(minHeight = minCellHeight)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: CalendarDay,
    displayedMonth: YearMonth,
    minCellHeight: androidx.compose.ui.unit.Dp,
    formatter: CalendarFormatter,
    locale: Locale,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val accessibilityDesc = remember(day, displayedMonth, locale) {
        formatter.formatDayAccessibilityDescription(day, displayedMonth, locale)
    }

    val backgroundColor = when {
        day.isSelected -> MaterialTheme.colorScheme.primary
        day.isToday -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
        else -> Color.Transparent
    }

    val contentColor = when {
        day.isSelected -> MaterialTheme.colorScheme.onPrimary
        day.isToday -> MaterialTheme.colorScheme.primary
        day.isCurrentMonth -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.42f)
    }

    val borderModifier = when {
        day.isToday && day.isSelected -> Modifier.border(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f),
            shape = RoundedCornerShape(10.dp)
        )
        day.isToday -> Modifier.border(
            width = 1.5.dp,
            color = MaterialTheme.colorScheme.primary,
            shape = RoundedCornerShape(10.dp)
        )
        else -> Modifier
    }

    Box(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .defaultMinSize(minWidth = 44.dp, minHeight = minCellHeight)
            .clip(RoundedCornerShape(10.dp))
            .background(backgroundColor, RoundedCornerShape(10.dp))
            .then(borderModifier)
            .semantics(mergeDescendants = true) {
                contentDescription = accessibilityDesc
            }
            .clickable(
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = when {
                        day.isSelected || day.isToday -> FontWeight.Bold
                        day.isCurrentMonth -> FontWeight.Medium
                        else -> FontWeight.Normal
                    }
                ),
                color = contentColor,
                textAlign = TextAlign.Center
            )

            // Small Today indicator dot so Today & Selected remain visually distinct and combinable
            if (day.isToday) {
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .background(
                            color = if (day.isSelected) {
                                MaterialTheme.colorScheme.onPrimary
                            } else {
                                MaterialTheme.colorScheme.primary
                            },
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QuickActionsPanel(
    hasSelectedDate: Boolean,
    onTodayClick: () -> Unit,
    onCopyCalendarClick: () -> Unit,
    onCopyDateClick: () -> Unit,
    onSaveClick: () -> Unit,
    onShareClick: () -> Unit,
    onResetClick: () -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            onClick = onTodayClick,
            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Today,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("TODAY", style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
        }

        FilledTonalButton(
            onClick = onCopyCalendarClick,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("COPY CALENDAR", style = MaterialTheme.typography.labelMedium)
        }

        if (hasSelectedDate) {
            FilledTonalButton(
                onClick = onCopyDateClick,
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("COPY DATE", style = MaterialTheme.typography.labelMedium)
            }
        }

        FilledTonalButton(
            onClick = onSaveClick,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("SAVE", style = MaterialTheme.typography.labelMedium)
        }

        OutlinedButton(
            onClick = onShareClick,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("SHARE", style = MaterialTheme.typography.labelMedium)
        }

        OutlinedButton(
            onClick = onResetClick,
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("RESET", style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun SelectedDateDetailsCard(
    details: DateDetails,
    selectedWeek: CalendarWeek?,
    orderedWeekdays: List<DayOfWeek>,
    dateFormat: DateFormatOption,
    showWeekView: Boolean,
    showDayOfYear: Boolean,
    showLeapYear: Boolean,
    showIsoWeek: Boolean,
    formatter: CalendarFormatter,
    locale: Locale,
    onSelectDayFromWeek: (LocalDate) -> Unit,
    onCopyDate: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(
                        text = "SELECTED DATE",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = formatter.formatWeekdayFull(details.dayOfWeek, locale),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatter.formatDate(details.date, dateFormat, locale),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                TextButton(onClick = onCopyDate) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("COPY DATE", style = MaterialTheme.typography.labelSmall)
                }
            }

            // Compact summary line: Day 297 of 365 • Week 43
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Day ${details.dayOfYear} of ${details.lengthOfYear}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Week ${details.localeWeekOfYear}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Optional THIS WEEK strip
            if (showWeekView && selectedWeek != null) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .padding(10.dp)
                ) {
                    Text(
                        text = "THIS WEEK",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        orderedWeekdays.forEach { dow ->
                            Text(
                                text = formatter.formatWeekdayShort(dow, locale, uppercase = false),
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        selectedWeek.days.forEach { dayItem ->
                            val isSelected = dayItem.date == details.date
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                    .clickable { onSelectDayFromWeek(dayItem.date) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isSelected) "[${dayItem.date.dayOfMonth}]" else dayItem.date.dayOfMonth.toString(),
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                    ),
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimary
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

            Text(
                text = "DATE DETAILS",
                style = MaterialTheme.typography.labelSmall.copy(
                    letterSpacing = 1.sp,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Compact 2-column Date Details grid
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                if (showDayOfYear) {
                    DetailMetricRow(
                        label = "Day of year",
                        value = "${details.dayOfYear}"
                    )
                }
                DetailMetricRow(
                    label = "Week of year",
                    value = "${details.localeWeekOfYear}"
                )
                if (showIsoWeek) {
                    val isoLabel = if (details.isoWeekYear != details.date.year) {
                        "${details.isoWeekOfYear} (${details.isoWeekYear})"
                    } else {
                        "${details.isoWeekOfYear}"
                    }
                    DetailMetricRow(
                        label = "ISO week",
                        value = isoLabel
                    )
                } else {
                    DetailMetricRow(
                        label = "ISO week",
                        value = "${details.isoWeekOfYear}"
                    )
                }
                DetailMetricRow(
                    label = "Day of month",
                    value = "${details.dayOfMonth}"
                )
                DetailMetricRow(
                    label = "Days remaining",
                    value = "${details.daysRemainingInYear} days remaining"
                )
                if (showLeapYear) {
                    DetailMetricRow(
                        label = "Leap year",
                        value = if (details.isLeapYear) "Yes (${details.lengthOfYear} days)" else "No"
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthSummaryAndDetailsCard(
    uiState: MonthGridUiState,
    expanded: Boolean,
    animationsEnabled: Boolean,
    formatter: CalendarFormatter,
    locale: Locale,
    onToggleExpand: () -> Unit
) {
    val startWeekday = formatter.formatWeekdayFull(uiState.monthStartDay, locale)
    val endWeekday = formatter.formatWeekdayFull(uiState.monthEndDay, locale)
    val monthYearTitle = formatter.formatMonthYear(uiState.currentMonth, locale)
    val monthName = formatter.formatMonthName(uiState.currentMonth, locale)
    val firstDate = uiState.currentMonth.atDay(1)
    val lastDate = uiState.currentMonth.atEndOfMonth()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = monthYearTitle,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${uiState.monthDays} days • ${uiState.weeksShown} weeks shown • Starts $startWeekday • Ends $endWeekday",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse month details" else "Expand month details",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = expanded,
                enter = expandVertically(tween(if (animationsEnabled) 240 else 0)) +
                    fadeIn(tween(if (animationsEnabled) 240 else 0)),
                exit = shrinkVertically(tween(if (animationsEnabled) 200 else 0)) +
                    fadeOut(tween(if (animationsEnabled) 200 else 0))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "MONTH DETAILS",
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 1.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    DetailMetricRow(label = "Month", value = monthName)
                    DetailMetricRow(label = "Days in month", value = "${uiState.monthDays}")
                    DetailMetricRow(label = "Starts", value = startWeekday)
                    DetailMetricRow(label = "Ends", value = endWeekday)
                    DetailMetricRow(
                        label = "MONTH START",
                        value = formatter.formatShortDayAndMonth(firstDate, locale)
                    )
                    DetailMetricRow(
                        label = "MONTH END",
                        value = formatter.formatShortDayAndMonth(lastDate, locale)
                    )
                    DetailMetricRow(label = "Calendar rows", value = "${uiState.weeksShown}")
                    if (uiState.settings.showLeapYear) {
                        DetailMetricRow(
                            label = "Leap year",
                            value = if (uiState.isCurrentMonthLeapYear) "Yes" else "No"
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailMetricRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
