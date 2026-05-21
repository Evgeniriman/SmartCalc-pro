package com.example.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsRepository(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)

    // 0: System, 1: Light, 2: Dark
    private val _themeFlow = MutableStateFlow(prefs.getInt("theme", 0)) 
    val themeFlow: StateFlow<Int> = _themeFlow.asStateFlow()

    private val _dynamicColorFlow = MutableStateFlow(prefs.getBoolean("dynamic_color", true))
    val dynamicColorFlow: StateFlow<Boolean> = _dynamicColorFlow.asStateFlow()

    // 0: Circle, 1: Rounded Rectangle, 2: Squircle
    private val _buttonShapeFlow = MutableStateFlow(prefs.getInt("button_shape", 0))
    val buttonShapeFlow: StateFlow<Int> = _buttonShapeFlow.asStateFlow()

    private val _hapticFeedbackFlow = MutableStateFlow(prefs.getBoolean("haptic_feedback", true))
    val hapticFeedbackFlow: StateFlow<Boolean> = _hapticFeedbackFlow.asStateFlow()

    // 0: Auto, 1: 2 places, 2: 4 places, 3: 6 places
    private val _decimalPlacesFlow = MutableStateFlow(prefs.getInt("decimal_places", 0))
    val decimalPlacesFlow: StateFlow<Int> = _decimalPlacesFlow.asStateFlow()

    // 0: Degrees, 1: Radians
    private val _angleUnitFlow = MutableStateFlow(prefs.getInt("angle_unit", 0))
    val angleUnitFlow: StateFlow<Int> = _angleUnitFlow.asStateFlow()

    // 0: Classic Lavender, 1: Nordic Forest, 2: Deep Crimson, 3: Ocean Mist, 4: Amber Horizon
    private val _colorPresetFlow = MutableStateFlow(prefs.getInt("color_preset", 0))
    val colorPresetFlow: StateFlow<Int> = _colorPresetFlow.asStateFlow()

    // 0: Compact (4dp), 1: Standard (8dp), 2: Comfortable (12dp)
    private val _spacingPresetFlow = MutableStateFlow(prefs.getInt("spacing_preset", 1))
    val spacingPresetFlow: StateFlow<Int> = _spacingPresetFlow.asStateFlow()

    // 0: Modern Sans, 1: Sci-Fi Monospace, 2: Classic Serif
    private val _fontPresetFlow = MutableStateFlow(prefs.getInt("font_preset", 0))
    val fontPresetFlow: StateFlow<Int> = _fontPresetFlow.asStateFlow()

    fun setTheme(theme: Int) {
        prefs.edit().putInt("theme", theme).apply()
        _themeFlow.value = theme
    }

    fun setDynamicColor(dynamicColor: Boolean) {
        prefs.edit().putBoolean("dynamic_color", dynamicColor).apply()
        _dynamicColorFlow.value = dynamicColor
    }

    fun setButtonShape(shape: Int) {
        prefs.edit().putInt("button_shape", shape).apply()
        _buttonShapeFlow.value = shape
    }

    fun setHapticFeedback(enabled: Boolean) {
        prefs.edit().putBoolean("haptic_feedback", enabled).apply()
        _hapticFeedbackFlow.value = enabled
    }

    fun setDecimalPlaces(places: Int) {
        prefs.edit().putInt("decimal_places", places).apply()
        _decimalPlacesFlow.value = places
    }

    fun setAngleUnit(unit: Int) {
        prefs.edit().putInt("angle_unit", unit).apply()
        _angleUnitFlow.value = unit
    }

    fun setColorPreset(preset: Int) {
        prefs.edit().putInt("color_preset", preset).apply()
        _colorPresetFlow.value = preset
    }

    fun setSpacingPreset(preset: Int) {
        prefs.edit().putInt("spacing_preset", preset).apply()
        _spacingPresetFlow.value = preset
    }

    fun setFontPreset(preset: Int) {
        prefs.edit().putInt("font_preset", preset).apply()
        _fontPresetFlow.value = preset
    }
}
