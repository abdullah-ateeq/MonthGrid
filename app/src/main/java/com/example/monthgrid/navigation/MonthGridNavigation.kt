package com.example.monthgrid.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.monthgrid.formatter.CalendarFormatter
import com.example.monthgrid.ui.MonthGridViewModel
import com.example.monthgrid.ui.SplashScreen
import com.example.monthgrid.ui.about.AboutScreen
import com.example.monthgrid.ui.history.HistoryScreen
import com.example.monthgrid.ui.home.HomeScreen
import com.example.monthgrid.ui.settings.SettingsScreen
import com.example.monthgrid.util.SystemActionHelper
import kotlinx.coroutines.launch

enum class MonthGridScreen(
    val route: String,
    val title: String,
    val icon: ImageVector? = null
) {
    Splash(route = "splash", title = "Splash"),
    Home(route = "home", title = "Home", icon = Icons.Default.CalendarMonth),
    History(route = "history", title = "History", icon = Icons.Default.History),
    Settings(route = "settings", title = "Settings", icon = Icons.Default.Settings),
    About(route = "about", title = "About", icon = Icons.Default.Info)
}

@Composable
fun MonthGridAppHost(
    viewModel: MonthGridViewModel,
    startDestination: String = MonthGridScreen.Splash.route
) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsState()
    val formatter = remember { CalendarFormatter() }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavScreens = remember {
        listOf(
            MonthGridScreen.Home,
            MonthGridScreen.History,
            MonthGridScreen.Settings,
            MonthGridScreen.About
        )
    }

    LaunchedEffect(uiState.statusMessage, uiState.errorMessage) {
        val message = uiState.errorMessage ?: uiState.statusMessage
        if (!message.isNullOrBlank()) {
            snackbarHostState.showSnackbar(
                message = message,
                duration = SnackbarDuration.Short
            )
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            if (currentRoute != null && currentRoute != MonthGridScreen.Splash.route) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        val selected = currentRoute == screen.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                if (!selected) {
                                    SystemActionHelper.performLightHaptic(
                                        view,
                                        uiState.settings.hapticFeedback
                                    )
                                    navController.navigate(screen.route) {
                                        popUpTo(MonthGridScreen.Home.route) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            },
                            icon = {
                                screen.icon?.let { iconVector ->
                                    Icon(
                                        imageVector = iconVector,
                                        contentDescription = screen.title
                                    )
                                }
                            },
                            label = { Text(screen.title) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MonthGridScreen.Splash.route) {
                SplashScreen(
                    animationsEnabled = uiState.settings.animations,
                    onSplashFinished = {
                        navController.navigate(MonthGridScreen.Home.route) {
                            popUpTo(MonthGridScreen.Splash.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(MonthGridScreen.Home.route) {
                HomeScreen(
                    uiState = uiState,
                    formatter = formatter,
                    onPreviousMonth = viewModel::previousMonth,
                    onNextMonth = viewModel::nextMonth,
                    onPreviousYear = viewModel::previousYear,
                    onNextYear = viewModel::nextYear,
                    onSelectMonthYear = viewModel::selectMonth,
                    onSelectDate = viewModel::selectDate,
                    onJumpToDate = viewModel::jumpToDate,
                    onTodayClick = viewModel::jumpToToday,
                    onSaveHistory = viewModel::saveCurrentMonthToHistory,
                    onReset = viewModel::resetToDefaults,
                    onCopyCalendar = viewModel::getPlainTextCalendar,
                    onCopySelectedDate = viewModel::getFormattedSelectedDate,
                    onShowFeedback = { msg ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = msg,
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }

            composable(MonthGridScreen.History.route) {
                HistoryScreen(
                    history = uiState.history,
                    confirmDelete = uiState.settings.confirmDelete,
                    hapticsEnabled = uiState.settings.hapticFeedback,
                    dateFormat = uiState.settings.dateFormat,
                    formatter = formatter,
                    onRestoreItem = { item ->
                        viewModel.restoreFromHistory(item)
                        navController.navigate(MonthGridScreen.Home.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                        }
                    },
                    onDeleteItem = viewModel::deleteHistoryItem,
                    onClearAll = viewModel::clearAllHistory
                )
            }

            composable(MonthGridScreen.Settings.route) {
                SettingsScreen(
                    settings = uiState.settings,
                    onThemeModeChange = viewModel::setThemeMode,
                    onDynamicColorChange = viewModel::setDynamicColor,
                    onWeekStartChange = viewModel::setWeekStart,
                    onWeekNumberingChange = viewModel::setWeekNumbering,
                    onShowWeekNumbersChange = viewModel::setShowWeekNumbers,
                    onShowAdjacentMonthDaysChange = viewModel::setShowAdjacentMonthDays,
                    onCalendarDensityChange = viewModel::setCalendarDensity,
                    onShowDayDetailsChange = viewModel::setShowDayDetails,
                    onShowWeekViewChange = viewModel::setShowWeekView,
                    onShowMonthProgressChange = viewModel::setShowMonthProgress,
                    onShowLeapYearChange = viewModel::setShowLeapYear,
                    onShowDayOfYearChange = viewModel::setShowDayOfYear,
                    onShowIsoWeekChange = viewModel::setShowIsoWeek,
                    onDateFormatChange = viewModel::setDateFormat,
                    onHapticFeedbackChange = viewModel::setHapticFeedback,
                    onAnimationsChange = viewModel::setAnimations,
                    onConfirmDeleteChange = viewModel::setConfirmDelete,
                    onRestoreLastMonthChange = viewModel::setRestoreLastMonth,
                    onResetSettings = viewModel::resetSettingsOnly,
                    onClearHistory = viewModel::clearAllHistory
                )
            }

            composable(MonthGridScreen.About.route) {
                AboutScreen()
            }
        }
    }
}
