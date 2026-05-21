package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel,
    onNavigateBack: () -> Unit
) {
    val theme by viewModel.themeFlow.collectAsStateWithLifecycle()
    val dynamicColor by viewModel.dynamicColorFlow.collectAsStateWithLifecycle()
    val buttonShape by viewModel.buttonShapeFlow.collectAsStateWithLifecycle()
    val hapticFeedback by viewModel.hapticFeedbackFlow.collectAsStateWithLifecycle()
    val decimalPlaces by viewModel.decimalPlacesFlow.collectAsStateWithLifecycle()
    val angleUnit by viewModel.angleUnitFlow.collectAsStateWithLifecycle()
    val colorPreset by viewModel.colorPresetFlow.collectAsStateWithLifecycle()
    val spacingPreset by viewModel.spacingPresetFlow.collectAsStateWithLifecycle()
    val fontPreset by viewModel.fontPresetFlow.collectAsStateWithLifecycle()

    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        Text(
                            "Settings & Tuning", 
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp 
                        ) 
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("Appearance", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                        icon = { Icon(Icons.Default.Palette, contentDescription = "Visuals") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("Engine / Math", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                        icon = { Icon(Icons.Default.Settings, contentDescription = "Engine") }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = { Text("Release Log & About", fontWeight = FontWeight.SemiBold, fontSize = 13.sp) },
                        icon = { Icon(Icons.Default.Info, contentDescription = "Docs") }
                    )
                }
            }
        }
    ) { paddingValues ->
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220)) + slideInHorizontally(
                    animationSpec = tween(220),
                    initialOffsetX = { if (targetState > initialState) it else -it }
                )) togetherWith (fadeOut(animationSpec = tween(180)) + slideOutHorizontally(
                    animationSpec = tween(180),
                    targetOffsetX = { if (targetState > initialState) -it else it }
                ))
            },
            label = "settings_tabs_transition",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) { tabIndex ->
            when (tabIndex) {
                0 -> {
                    // TAB 0: Appearance / Visuals Tuning
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 1. Theme Selector
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Palette, 
                                            contentDescription = null, 
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Application Theme Mode",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Toggle between light, dark, or system preferences.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Column(Modifier.selectableGroup()) {
                                        val themeOptions = listOf("System Adaptive", "Light Theme", "Dark Theme")
                                        themeOptions.forEachIndexed { index, option ->
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp)
                                                    .selectable(
                                                        selected = (theme == index),
                                                        onClick = { viewModel.setTheme(index) },
                                                        role = Role.RadioButton
                                                    ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (theme == index),
                                                    onClick = null 
                                                )
                                                Spacer(Modifier.width(16.dp))
                                                Text(text = option, style = MaterialTheme.typography.bodyLarge)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Dynamic Wallpaper Theme Selector (Material You)
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.ColorLens,
                                                contentDescription = null,
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Material You Palette", 
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Incorporate device wallpaper colors directly into the calculator buttons.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = dynamicColor,
                                        onCheckedChange = { viewModel.setDynamicColor(it) }
                                    )
                                }
                            }
                        }

                        // 2.5 Color Theme Presets Selection (Artisan Presets)
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Palette, 
                                            contentDescription = null, 
                                            tint = if (dynamicColor) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Artisan Color Theme Presets",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = if (dynamicColor) MaterialTheme.colorScheme.primary.copy(alpha = 0.4f) else MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = if (dynamicColor) 
                                            "System dynamic wallpaper active. Turn off 'Material You Palette' above to enable these artisan presets." 
                                            else "Choose a masterfully pre-designed color palette theme.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (dynamicColor) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    val presets = listOf(
                                        "Classic Lavender (Default)", 
                                        "Nordic Forest (Sage & Mint)", 
                                        "Crimson Eclipse (Charcoal & Red)", 
                                        "Ocean Mist (Cyber Cyan)", 
                                        "Amber Horizon (Warm Sunset)"
                                    )
                                    Column(Modifier.selectableGroup()) {
                                        presets.forEachIndexed { index, option ->
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp)
                                                    .selectable(
                                                        selected = (colorPreset == index),
                                                        onClick = { if (!dynamicColor) viewModel.setColorPreset(index) },
                                                        role = Role.RadioButton,
                                                        enabled = !dynamicColor
                                                    ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (colorPreset == index),
                                                    onClick = null,
                                                    enabled = !dynamicColor
                                                )
                                                Spacer(Modifier.width(16.dp))
                                                Text(
                                                    text = option, 
                                                    style = MaterialTheme.typography.bodyLarge,
                                                    color = if (dynamicColor) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f) else MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Button Shapes Customizer + Aesthetic Preview
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Category,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Keypad Button Geometry",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Choose the styling contour applied to all physical calculator pads.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Column(Modifier.selectableGroup()) {
                                        val shapeOptions = listOf("Classic Circle", "Rounded Corner Core", "Trendy Squircle")
                                        shapeOptions.forEachIndexed { index, option ->
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp)
                                                    .selectable(
                                                        selected = (buttonShape == index),
                                                        onClick = { viewModel.setButtonShape(index) },
                                                        role = Role.RadioButton
                                                    ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (buttonShape == index),
                                                    onClick = null
                                                )
                                                Spacer(Modifier.width(16.dp))
                                                Text(text = option, style = MaterialTheme.typography.bodyLarge)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        "Visual Preview:", 
                                        style = MaterialTheme.typography.labelLarge, 
                                        color = MaterialTheme.colorScheme.secondary, 
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 8.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        CalculatorButton("7", buttonShapeId = buttonShape, modifier = Modifier.weight(1f)) {}
                                        CalculatorButton("8", buttonShapeId = buttonShape, modifier = Modifier.weight(1f)) {}
                                        CalculatorButton("√", buttonShapeId = buttonShape, isAction = true, modifier = Modifier.weight(1f)) {}
                                        CalculatorButton("=", buttonShapeId = buttonShape, isPrimary = true, modifier = Modifier.weight(1f)) {}
                                    }
                                }
                            }
                        }

                        // 4. Button Spacing & Grid Density
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.List,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Keypad Row Spacing & Density",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Adjust the spacing buffer surrounding layout buttons.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    SingleChoiceSegmentedButtonRow(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        val densityOptions = listOf("Compact (4dp)", "Standard (8dp)", "Comfortable (12dp)")
                                        densityOptions.forEachIndexed { index, label ->
                                            SegmentedButton(
                                                shape = SegmentedButtonDefaults.itemShape(index = index, count = densityOptions.size),
                                                onClick = { viewModel.setSpacingPreset(index) },
                                                selected = spacingPreset == index
                                            ) {
                                                Text(label, fontSize = 11.sp, maxLines = 1)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 5. Display Typography Selector
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Calculator Font Typography",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Modify the layout typeface and font personality of the display values.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    Column(Modifier.selectableGroup()) {
                                        val fontOptions = listOf("Modern Sans-Serif", "Sci-Fi Monospace", "Classic Editorial Serif")
                                        fontOptions.forEachIndexed { index, option ->
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp)
                                                    .selectable(
                                                        selected = (fontPreset == index),
                                                        onClick = { viewModel.setFontPreset(index) },
                                                        role = Role.RadioButton
                                                    ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (fontPreset == index),
                                                    onClick = null
                                                )
                                                Spacer(Modifier.width(16.dp))
                                                val previewFamily = when (index) {
                                                    1 -> FontFamily.Monospace
                                                    2 -> FontFamily.Serif
                                                    else -> FontFamily.Default
                                                }
                                                Text(
                                                    text = option, 
                                                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = previewFamily)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // TAB 1: Engine Calculations & Mathematical Tuning
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        // 1. Angle Measurement Unit
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.ChangeHistory, 
                                            contentDescription = null, 
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Trigonometric Angle Unit",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Determine how sine, cosine, and tangent interpret values.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    
                                    Column(Modifier.selectableGroup()) {
                                        val angleOptions = listOf("Degrees (e.g. sin(90) = 1)", "Radians (e.g. sin(π/2) = 1)")
                                        angleOptions.forEachIndexed { index, option ->
                                            Row(
                                                Modifier
                                                    .fillMaxWidth()
                                                    .height(48.dp)
                                                    .selectable(
                                                        selected = (angleUnit == index),
                                                        onClick = { viewModel.setAngleUnit(index) },
                                                        role = Role.RadioButton
                                                    ),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                RadioButton(
                                                    selected = (angleUnit == index),
                                                    onClick = null 
                                                )
                                                Spacer(Modifier.width(16.dp))
                                                Text(text = option, style = MaterialTheme.typography.bodyLarge)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 2. Calculation Precision length Segmented Selector
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            Icons.Default.Percent, 
                                            contentDescription = null, 
                                            tint = MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Answers Decimal Precision",
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Limit decimal places displayed in final division and equation answers.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    SingleChoiceSegmentedButtonRow(
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        val decimalOptions = listOf("Auto", "2 Dec", "4 Dec", "6 Dec")
                                        decimalOptions.forEachIndexed { index, label ->
                                            SegmentedButton(
                                                shape = SegmentedButtonDefaults.itemShape(index = index, count = decimalOptions.size),
                                                onClick = { viewModel.setDecimalPlaces(index) },
                                                selected = decimalPlaces == index
                                            ) {
                                                Text(label, fontSize = 11.sp, maxLines = 1)
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Dynamic Physical Haptic Switch
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                Icons.Default.Vibration, 
                                                contentDescription = null, 
                                                tint = MaterialTheme.colorScheme.primary,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                "Haptic Touch Response", 
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = "Produce real mechanical finger pulse triggers on keypress.",
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Switch(
                                        checked = hapticFeedback,
                                        onCheckedChange = { viewModel.setHapticFeedback(it) }
                                    )
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // TAB 2: Releases History Changelog & Developer Info
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // 1. App logo and branding
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = RoundedCornerShape(24.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.size(80.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                "=",
                                                style = MaterialTheme.typography.headlineLarge,
                                                fontSize = 42.sp,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "SmartCalc Pro",
                                        style = MaterialTheme.typography.headlineMedium,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Version 1.3.0 - Major Tuning",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // 2. Interactive Interactive Changelog Release History!
                        item {
                            Text(
                                text = "Engine Release History",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.secondary,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
                            )
                        }

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    ChangelogItem(
                                        version = "v1.3.0 (Current)",
                                        date = "May 2026",
                                        isLatest = true,
                                        features = listOf(
                                            "Added dynamic angle measurement units (Degrees vs. Radians) for trigonometric equations.",
                                            "Completely redesigned Settings into 3 custom tabs (Appearance, Engine, and Release Info).",
                                            "Created highly interactive chronological Changelog Logs timeline into Settings.",
                                            "Upgraded Material You responsive background rendering configurations."
                                        )
                                    )
                                    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                                    ChangelogItem(
                                        version = "v1.2.6",
                                        date = "April 2026",
                                        isLatest = false,
                                        features = listOf(
                                            "Embedded tactile haptic click micro-vibrations for responsive mechanical feeling.",
                                            "Introduced segmented calculation precision controller limits (Auto, 2, 4, 6 decimals)."
                                        )
                                    )
                                    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                                    ChangelogItem(
                                        version = "v1.1.0",
                                        date = "March 2026",
                                        isLatest = false,
                                        features = listOf(
                                            "Integrated offline Room database to safely archive expression histories.",
                                            "Constructed fully dynamic slide-in History and Equation Archive Screen."
                                        )
                                    )
                                    Divider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                                    ChangelogItem(
                                        version = "v1.0.0",
                                        date = "Jan 2026",
                                        isLatest = false,
                                        features = listOf(
                                            "Initial stable production deployment of SmartCalc Pro launcher UI.",
                                            "Material 3 adaptive design guidelines, squircle elements, and custom formulas parser."
                                        )
                                    )
                                }
                            }
                        }

                        // 3. Developer Credentials details Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(16.dp)
                                ) {
                                    AboutItem(
                                        icon = Icons.Filled.Info, 
                                        title = "What is SmartCalc?", 
                                        subtitle = "A sophisticated, highly interactive Material You 3 scientific computing program featuring live equation parsing, histories archiving, and physical layout customizers."
                                    )
                                    AboutItem(
                                        icon = Icons.Filled.Person, 
                                        title = "Lead Designer & Developer", 
                                        subtitle = "Evgeni Riman"
                                    )
                                    AboutItem(
                                        icon = Icons.Filled.Email, 
                                        title = "Developer Contact", 
                                        subtitle = "evgeniriman@gmail.com"
                                    )
                                    AboutItem(
                                        icon = Icons.Filled.Favorite, 
                                        title = "Engineering Support", 
                                        subtitle = "Engineered with maximum efficiency, deep care, and powered by Gemini AI's advanced code synthesis helper."
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChangelogItem(
    version: String,
    date: String,
    isLatest: Boolean,
    features: List<String>
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isLatest) MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = version,
                        color = if (isLatest) MaterialTheme.colorScheme.onPrimary 
                                else MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (isLatest) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFE53935))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            "NEW", 
                            color = Color.White, 
                            fontSize = 9.sp, 
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
            Text(
                text = date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        features.forEach { feature ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp, horizontal = 4.dp),
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    "•",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 8.dp),
                    fontSize = 14.sp
                )
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AboutItem(icon: ImageVector, title: String, subtitle: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon, 
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
