package com.example.monthgrid.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.monthgrid.model.CalendarDensity
import com.example.monthgrid.model.DateFormatOption
import com.example.monthgrid.model.MonthGridSettings
import com.example.monthgrid.model.ThemeMode
import com.example.monthgrid.model.WeekNumberingMode
import com.example.monthgrid.model.WeekStartOption
import com.example.monthgrid.util.SystemActionHelper

@Composable
fun SettingsScreen(
    settings: MonthGridSettings,
    onThemeModeChange: (ThemeMode) -> Unit,
    onDynamicColorChange: (Boolean) -> Unit,
    onWeekStartChange: (WeekStartOption) -> Unit,
    onWeekNumberingChange: (WeekNumberingMode) -> Unit,
    onShowWeekNumbersChange: (Boolean) -> Unit,
    onShowAdjacentMonthDaysChange: (Boolean) -> Unit,
    onCalendarDensityChange: (CalendarDensity) -> Unit,
    onShowDayDetailsChange: (Boolean) -> Unit,
    onShowWeekViewChange: (Boolean) -> Unit,
    onShowMonthProgressChange: (Boolean) -> Unit,
    onShowLeapYearChange: (Boolean) -> Unit,
    onShowDayOfYearChange: (Boolean) -> Unit,
    onShowIsoWeekChange: (Boolean) -> Unit,
    onDateFormatChange: (DateFormatOption) -> Unit,
    onHapticFeedbackChange: (Boolean) -> Unit,
    onAnimationsChange: (Boolean) -> Unit,
    onConfirmDeleteChange: (Boolean) -> Unit,
    onRestoreLastMonthChange: (Boolean) -> Unit,
    onResetSettings: () -> Unit,
    onClearHistory: () -> Unit
) {
    val view = LocalView.current
    val haptics = settings.hapticFeedback
    val scrollState = rememberScrollState()

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetSettingsDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Column {
            Text(
                text = "Settings",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Customize calendar grid appearance, numbering, and behavior.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // APPEARANCE
        SettingsSectionCard(title = "APPEARANCE") {
            SegmentedChoiceRow(
                label = "Theme",
                options = ThemeMode.entries,
                selected = settings.themeMode,
                optionLabel = { it.label },
                onSelect = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onThemeModeChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Dynamic Color",
                subtitle = "Use Material You wallpaper palette on Android 12+",
                checked = settings.dynamicColor,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onDynamicColorChange(it)
                }
            )
        }

        // CALENDAR SETTINGS
        SettingsSectionCard(title = "CALENDAR SETTINGS") {
            SegmentedChoiceRow(
                label = "Week Starts On",
                options = WeekStartOption.entries,
                selected = settings.weekStart,
                optionLabel = { it.label },
                onSelect = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onWeekStartChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            SegmentedChoiceRow(
                label = "Week Numbering",
                options = WeekNumberingMode.entries,
                selected = settings.weekNumbering,
                optionLabel = { it.label },
                onSelect = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onWeekNumberingChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Show Week Numbers",
                subtitle = "Display week number column at the start of each calendar row",
                checked = settings.showWeekNumbers,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onShowWeekNumbersChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Show Adjacent Month Days",
                subtitle = "Show muted dates from previous and next months to complete weeks",
                checked = settings.showAdjacentMonthDays,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onShowAdjacentMonthDaysChange(it)
                }
            )
        }

        // DISPLAY SETTINGS
        SettingsSectionCard(title = "DISPLAY SETTINGS") {
            SegmentedChoiceRow(
                label = "Calendar Density",
                options = CalendarDensity.entries,
                selected = settings.calendarDensity,
                optionLabel = { it.label },
                onSelect = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onCalendarDensityChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Show Day Details",
                subtitle = "Display detail card when a date is selected",
                checked = settings.showDayDetails,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onShowDayDetailsChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Show Week View",
                subtitle = "Show the 7-day week strip inside selected date details",
                checked = settings.showWeekView,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onShowWeekViewChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Show Month Progress",
                subtitle = "Subtle progress bar indicating day position in the month",
                checked = settings.showMonthProgress,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onShowMonthProgressChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Show Leap Year",
                subtitle = "Include leap year status in date and month details",
                checked = settings.showLeapYear,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onShowLeapYearChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Show Day of Year",
                subtitle = "Show day number within the 365/366-day year",
                checked = settings.showDayOfYear,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onShowDayOfYearChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Show ISO Week",
                subtitle = "Highlight ISO 8601 week and week-based year in details",
                checked = settings.showIsoWeek,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onShowIsoWeekChange(it)
                }
            )
        }

        // DATE FORMAT
        SettingsSectionCard(title = "DATE FORMAT") {
            SegmentedChoiceRow(
                label = "Formatted Date Style",
                options = DateFormatOption.entries,
                selected = settings.dateFormat,
                optionLabel = { it.label },
                onSelect = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onDateFormatChange(it)
                }
            )
        }

        // BEHAVIOR SETTINGS
        SettingsSectionCard(title = "BEHAVIOR SETTINGS") {
            ToggleSettingRow(
                title = "Haptic Feedback",
                subtitle = "Light tactile feedback when selecting dates or navigating",
                checked = settings.hapticFeedback,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, it)
                    onHapticFeedbackChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Animations",
                subtitle = "Smooth transitions when changing months and expanding details",
                checked = settings.animations,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onAnimationsChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Confirm Delete",
                subtitle = "Ask for confirmation before deleting saved history items",
                checked = settings.confirmDelete,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onConfirmDeleteChange(it)
                }
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
            ToggleSettingRow(
                title = "Restore Last Month",
                subtitle = "Reopen the last viewed month and selected date on launch",
                checked = settings.restoreLastMonth,
                onCheckedChange = {
                    SystemActionHelper.performLightHaptic(view, haptics)
                    onRestoreLastMonthChange(it)
                }
            )
        }

        // MANAGEMENT & RESET
        SettingsSectionCard(title = "DATA & DEFAULTS") {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        SystemActionHelper.performLightHaptic(view, haptics)
                        showResetSettingsDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reset Settings")
                }

                OutlinedButton(
                    onClick = {
                        SystemActionHelper.performLightHaptic(view, haptics)
                        showClearHistoryDialog = true
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Clear History")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("Clear all saved months?") },
            text = { Text("All saved months in History will be permanently removed.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        SystemActionHelper.performLightHaptic(view, haptics)
                        onClearHistory()
                        showClearHistoryDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showResetSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showResetSettingsDialog = false },
            title = { Text("Reset Settings?") },
            text = { Text("Restore all settings to their default values? Your saved history will be kept.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        SystemActionHelper.performLightHaptic(view, haptics)
                        onResetSettings()
                        showResetSettingsDialog = false
                    }
                ) {
                    Text("Reset")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetSettingsDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    content: @Composable () -> Unit
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                ),
                color = MaterialTheme.colorScheme.primary
            )
            content()
        }
    }
}

@Composable
private fun ToggleSettingRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun <T> SegmentedChoiceRow(
    label: String,
    options: List<T>,
    selected: T,
    optionLabel: (T) -> String,
    onSelect: (T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            options.forEach { option ->
                val isSelected = option == selected
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f)
                            }
                        )
                        .clickable { onSelect(option) }
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = optionLabel(option),
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
