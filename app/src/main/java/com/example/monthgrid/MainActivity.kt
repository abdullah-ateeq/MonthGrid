package com.example.monthgrid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.monthgrid.navigation.MonthGridAppHost
import com.example.monthgrid.ui.MonthGridViewModel
import com.example.monthgrid.ui.theme.MonthGridTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val monthGridViewModel: MonthGridViewModel = viewModel(
                factory = MonthGridViewModel.provideFactory(applicationContext)
            )
            val uiState by monthGridViewModel.uiState.collectAsState()

            MonthGridTheme(
                themeMode = uiState.settings.themeMode,
                dynamicColor = uiState.settings.dynamicColor
            ) {
                MonthGridAppHost(viewModel = monthGridViewModel)
            }
        }
    }
}
