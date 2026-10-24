package com.example.monthgrid

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.monthgrid.data.SharedPreferencesMonthGridStorage
import com.example.monthgrid.repository.MonthGridRepository
import com.example.monthgrid.ui.MonthGridViewModel
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.YearMonth

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContextAndVerifySharedPreferencesPersistence() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.monthgrid", appContext.packageName)

        val prefs = appContext.getSharedPreferences("test_month_grid_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().clear().commit()

        val storage = SharedPreferencesMonthGridStorage(prefs)
        val vm = MonthGridViewModel(
            repository = MonthGridRepository(storage),
            todayProvider = { LocalDate.of(2026, 10, 24) }
        )

        vm.selectMonth(YearMonth.of(2026, 10))
        vm.selectDate(LocalDate.of(2026, 10, 24))
        vm.saveCurrentMonthToHistory()

        val restartedVm = MonthGridViewModel(
            repository = MonthGridRepository(SharedPreferencesMonthGridStorage(prefs)),
            todayProvider = { LocalDate.of(2026, 10, 24) }
        )
        assertEquals(YearMonth.of(2026, 10), restartedVm.uiState.value.currentMonth)
        assertEquals(LocalDate.of(2026, 10, 24), restartedVm.uiState.value.selectedDate)
        assertEquals(1, restartedVm.uiState.value.history.size)
    }
}
