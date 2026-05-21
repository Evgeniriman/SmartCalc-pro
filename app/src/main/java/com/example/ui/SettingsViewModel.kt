package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.SettingsRepository

class SettingsViewModel(private val repository: SettingsRepository) : ViewModel() {
    val themeFlow = repository.themeFlow
    val dynamicColorFlow = repository.dynamicColorFlow
    val buttonShapeFlow = repository.buttonShapeFlow
    val hapticFeedbackFlow = repository.hapticFeedbackFlow
    val decimalPlacesFlow = repository.decimalPlacesFlow
    val angleUnitFlow = repository.angleUnitFlow
    val colorPresetFlow = repository.colorPresetFlow
    val spacingPresetFlow = repository.spacingPresetFlow
    val fontPresetFlow = repository.fontPresetFlow

    fun setTheme(theme: Int) {
        repository.setTheme(theme)
    }

    fun setDynamicColor(dynamicColor: Boolean) {
        repository.setDynamicColor(dynamicColor)
    }

    fun setButtonShape(shape: Int) {
        repository.setButtonShape(shape)
    }

    fun setHapticFeedback(enabled: Boolean) {
        repository.setHapticFeedback(enabled)
    }

    fun setDecimalPlaces(places: Int) {
        repository.setDecimalPlaces(places)
    }

    fun setAngleUnit(unit: Int) {
        repository.setAngleUnit(unit)
    }

    fun setColorPreset(preset: Int) {
        repository.setColorPreset(preset)
    }

    fun setSpacingPreset(preset: Int) {
        repository.setSpacingPreset(preset)
    }

    fun setFontPreset(preset: Int) {
        repository.setFontPreset(preset)
    }
}

class SettingsViewModelFactory(private val repository: SettingsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
