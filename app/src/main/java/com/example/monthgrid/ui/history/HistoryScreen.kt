package com.example.monthgrid.ui.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.monthgrid.formatter.CalendarFormatter
import com.example.monthgrid.model.DateFormatOption
import com.example.monthgrid.model.MonthGridHistory
import com.example.monthgrid.util.SystemActionHelper
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    history: List<MonthGridHistory>,
    confirmDelete: Boolean,
    hapticsEnabled: Boolean,
    dateFormat: DateFormatOption,
    formatter: CalendarFormatter,
    onRestoreItem: (MonthGridHistory) -> Unit,
    onDeleteItem: (String) -> Unit,
    onClearAll: () -> Unit
) {
    val view = LocalView.current
    val locale = Locale.getDefault()

    var pendingDeleteItem by remember { mutableStateOf<MonthGridHistory?>(null) }
    var showClearAllDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Saved Months",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Up to 10 saved monthly grids (${history.size}/10)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (history.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        showClearAllDialog = true
                    },
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("Clear All", style = MaterialTheme.typography.labelMedium)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (history.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                    Text(
                        text = "No Saved Months Yet",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Tap SAVE on the Home screen to store a month for quick access.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(
                    items = history,
                    key = { it.id }
                ) { item ->
                    val dismissState = rememberSwipeToDismissBoxState()

                    LaunchedEffect(dismissState.currentValue) {
                        val value = dismissState.currentValue
                        if (value == SwipeToDismissBoxValue.EndToStart ||
                            value == SwipeToDismissBoxValue.StartToEnd
                        ) {
                            SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                            if (confirmDelete) {
                                pendingDeleteItem = item
                                dismissState.reset()
                            } else {
                                onDeleteItem(item.id)
                            }
                        }
                    }

                    SwipeToDismissBox(
                        state = dismissState,
                        backgroundContent = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                        }
                    ) {
                        HistoryItemCard(
                            item = item,
                            dateFormat = dateFormat,
                            formatter = formatter,
                            locale = locale,
                            onClick = {
                                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                                onRestoreItem(item)
                            },
                            onDeleteClick = {
                                SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                                if (confirmDelete) {
                                    pendingDeleteItem = item
                                } else {
                                    onDeleteItem(item.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    if (pendingDeleteItem != null) {
        val target = pendingDeleteItem!!
        AlertDialog(
            onDismissRequest = { pendingDeleteItem = null },
            title = { Text("Delete this saved month?") },
            text = {
                Text(
                    "Remove ${formatter.formatMonthYear(target.yearMonth, locale)} from your saved history?"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        onDeleteItem(target.id)
                        pendingDeleteItem = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteItem = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showClearAllDialog) {
        AlertDialog(
            onDismissRequest = { showClearAllDialog = false },
            title = { Text("Clear all saved months?") },
            text = { Text("This will remove all saved months from History.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        SystemActionHelper.performLightHaptic(view, hapticsEnabled)
                        onClearAll()
                        showClearAllDialog = false
                    }
                ) {
                    Text("Clear", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HistoryItemCard(
    item: MonthGridHistory,
    dateFormat: DateFormatOption,
    formatter: CalendarFormatter,
    locale: Locale,
    onClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val yearMonth = item.yearMonth
    val daysInMonth = yearMonth.lengthOfMonth()
    val startWeekday = formatter.formatWeekdayFull(yearMonth.atDay(1).dayOfWeek, locale)
    val endWeekday = formatter.formatWeekdayFull(yearMonth.atEndOfMonth().dayOfWeek, locale)
    val savedLabel = remember(item.savedAt, locale) {
        formatSavedAtLabel(item.savedAt, locale)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = formatter.formatMonthYear(yearMonth, locale),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "$daysInMonth days • Starts $startWeekday • Ends $endWeekday",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                val parsedSelected = item.parsedSelectedDate
                if (parsedSelected != null) {
                    Text(
                        text = "Selected date: ${formatter.formatDate(parsedSelected, dateFormat, locale)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Medium
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    text = savedLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                )
            }

            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete saved month",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

private fun formatSavedAtLabel(savedAtMillis: Long, locale: Locale): String {
    if (savedAtMillis <= 0L) return "Saved"
    return runCatching {
        val zone = ZoneId.systemDefault()
        val savedDateTime = Instant.ofEpochMilli(savedAtMillis).atZone(zone)
        val today = LocalDate.now(zone)
        val timeText = savedDateTime.format(DateTimeFormatter.ofPattern("HH:mm", locale))
        if (savedDateTime.toLocalDate() == today) {
            "Saved today at $timeText"
        } else {
            val dateText = savedDateTime.format(DateTimeFormatter.ofPattern("d MMM yyyy", locale))
            "Saved $dateText"
        }
    }.getOrDefault("Saved")
}
