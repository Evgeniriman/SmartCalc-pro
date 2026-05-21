package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.AppDatabase
import com.example.data.HistoryRepository
import com.example.data.SettingsRepository
import com.example.ui.CalculatorScreen
import com.example.ui.CalculatorViewModel
import com.example.ui.CalculatorViewModelFactory
import com.example.ui.SettingsScreen
import com.example.ui.SettingsViewModel
import com.example.ui.SettingsViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val database = AppDatabase.getDatabase(this)
    val historyRepository = HistoryRepository(database.historyDao())
    val factory = CalculatorViewModelFactory(historyRepository)
    val viewModel: CalculatorViewModel by viewModels { factory }

    val settingsRepository = SettingsRepository(this)
    val settingsFactory = SettingsViewModelFactory(settingsRepository)
    val settingsViewModel: SettingsViewModel by viewModels { settingsFactory }

    enableEdgeToEdge()
    setContent {
      val theme by settingsViewModel.themeFlow.collectAsStateWithLifecycle()
      val dynamicColor by settingsViewModel.dynamicColorFlow.collectAsStateWithLifecycle()
      val buttonShape by settingsViewModel.buttonShapeFlow.collectAsStateWithLifecycle()
      val decimalPlaces by settingsViewModel.decimalPlacesFlow.collectAsStateWithLifecycle()
      val hapticFeedback by settingsViewModel.hapticFeedbackFlow.collectAsStateWithLifecycle()
      val angleUnit by settingsViewModel.angleUnitFlow.collectAsStateWithLifecycle()
      val colorPreset by settingsViewModel.colorPresetFlow.collectAsStateWithLifecycle()
      val fontPreset by settingsViewModel.fontPresetFlow.collectAsStateWithLifecycle()
      val spacingPreset by settingsViewModel.spacingPresetFlow.collectAsStateWithLifecycle()

      val isDarkTheme = when (theme) {
          1 -> false
          2 -> true
          else -> isSystemInDarkTheme()
      }

      MyApplicationTheme(
          darkTheme = isDarkTheme,
          dynamicColor = dynamicColor,
          colorPreset = colorPreset,
          fontPreset = fontPreset
      ) {
        Surface(modifier = Modifier.fillMaxSize()) {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "calculator") {
                composable("calculator") {
                    CalculatorScreen(
                        viewModel = viewModel,
                        buttonShape = buttonShape,
                        decimalPlaces = decimalPlaces,
                        angleUnit = angleUnit,
                        spacingPreset = spacingPreset,
                        hapticFeedbackEnabled = hapticFeedback,
                        onNavigateToSettings = { navController.navigate("settings") }
                    )
                }
                composable("settings") {
                    SettingsScreen(
                        viewModel = settingsViewModel,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
        }
      }
    }
  }
}
