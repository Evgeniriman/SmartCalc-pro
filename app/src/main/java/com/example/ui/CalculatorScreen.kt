package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.compose.collectAsStateWithLifecycle

val LocalHapticFeedbackEnabled = staticCompositionLocalOf { true }
val LocalDecimalPlaces = staticCompositionLocalOf { 0 }
val LocalAngleUnit = staticCompositionLocalOf { 0 }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    viewModel: CalculatorViewModel,
    buttonShape: Int = 0,
    decimalPlaces: Int = 0,
    angleUnit: Int = 0,
    spacingPreset: Int = 1,
    hapticFeedbackEnabled: Boolean = true,
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val history by viewModel.history.collectAsStateWithLifecycle()
    var showHistory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calculator") },
                actions = {
                    IconButton(onClick = { showHistory = !showHistory }) {
                        Icon(Icons.Filled.History, contentDescription = "History")
                    }
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { paddingValues ->
        CompositionLocalProvider(
            LocalHapticFeedbackEnabled provides hapticFeedbackEnabled,
            LocalDecimalPlaces provides decimalPlaces,
            LocalAngleUnit provides angleUnit
        ) {
            if (showHistory) {
                HistoryScreen(
                    history = history,
                    onClearHistory = { viewModel.onAction(CalculatorAction.ClearHistory) },
                    onClose = { showHistory = false },
                    modifier = Modifier.padding(paddingValues)
                )
            } else {
                CalculatorLayout(
                    uiState = uiState,
                    buttonShapeId = buttonShape,
                    spacingPreset = spacingPreset,
                    onAction = viewModel::onAction,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
fun HistoryScreen(
    history: List<com.example.data.CalculationHistory>,
    onClearHistory: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("History", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = onClearHistory) {
                Text("Clear")
            }
        }
        
        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No history yet.", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(history) { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = item.expression,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "= ${item.result}",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorLayout(
    uiState: CalculatorUiState,
    buttonShapeId: Int = 0,
    spacingPreset: Int = 1,
    onAction: (CalculatorAction) -> Unit,
    modifier: Modifier = Modifier
) {
    var mode by remember { mutableStateOf(0) }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        val isCompactHeight = run {
            val h = maxHeight.value
            val w = maxWidth.value
            if (h.isInfinite() || h.isNaN() || w.isInfinite() || w.isNaN() || h <= 0f || w <= 0f) {
                false
            } else {
                maxHeight < 680.dp || (h / w.coerceAtLeast(1f)) < 1.51f
            }
        }
        
        val outerPadding = if (isCompactHeight) 8.dp else 16.dp
        val displayBottomPadding = if (isCompactHeight) 4.dp else 16.dp
        val segmentedBottomPadding = if (isCompactHeight) 6.dp else 16.dp
        
        val expressionSize = if (isCompactHeight) 28.sp else 48.sp
        val expressionLineHeight = if (isCompactHeight) 32.sp else 40.sp
        val resultSize = if (isCompactHeight) 36.sp else 56.sp
        
        val buttonSpacing = when (spacingPreset) {
            0 -> 4.dp
            2 -> if (isCompactHeight) 8.dp else 12.dp
            else -> if (isCompactHeight) 6.dp else 8.dp
        }
        val buttonRatio = if (isCompactHeight) 1.25f else 1.0f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(outerPadding),
            verticalArrangement = Arrangement.Bottom
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(bottom = displayBottomPadding),
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = uiState.expression,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.End,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = expressionLineHeight,
                    fontSize = expressionSize,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(if (isCompactHeight) 4.dp else 8.dp))
                AnimatedContent(
                    targetState = uiState.result,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith fadeOut(animationSpec = tween(200))
                    },
                    label = "result_animation"
                ) { targetResult ->
                    Text(
                        text = targetResult,
                        style = MaterialTheme.typography.displayMedium,
                        color = if (uiState.isError) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        fontSize = resultSize,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = segmentedBottomPadding)
            ) {
                val options = listOf("Standard", "Advanced")
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                        onClick = { mode = index },
                        selected = mode == index
                    ) {
                        Text(label, fontSize = if (isCompactHeight) 12.sp else 14.sp)
                    }
                }
            }

            AnimatedContent(
                targetState = mode,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "calculator_mode"
            ) { targetMode ->
                if (targetMode == 0) {
                    StandardPad(
                        buttonShapeId = buttonShapeId, 
                        buttonSpacing = buttonSpacing, 
                        buttonRatio = buttonRatio,
                        isCompact = isCompactHeight,
                        onAction = onAction
                    )
                } else {
                    AdvancedPad(
                        buttonShapeId = buttonShapeId, 
                        buttonSpacing = buttonSpacing, 
                        buttonRatio = buttonRatio,
                        isCompact = isCompactHeight,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Composable
fun StandardPad(
    buttonShapeId: Int,
    buttonSpacing: androidx.compose.ui.unit.Dp,
    buttonRatio: Float,
    isCompact: Boolean,
    onAction: (CalculatorAction) -> Unit
) {
    val decimalPlaces = LocalDecimalPlaces.current
    val angleUnit = LocalAngleUnit.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("C", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Clear) }
            CalculatorButton("(", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("(")) }
            CalculatorButton(")", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator(")")) }
            CalculatorButton("/", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("/")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("7", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(7)) }
            CalculatorButton("8", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(8)) }
            CalculatorButton("9", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(9)) }
            CalculatorButton("*", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("*")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("4", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(4)) }
            CalculatorButton("5", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(5)) }
            CalculatorButton("6", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(6)) }
            CalculatorButton("-", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("-")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("1", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(1)) }
            CalculatorButton("2", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(2)) }
            CalculatorButton("3", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(3)) }
            CalculatorButton("+", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("+")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("0", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(0)) }
            CalculatorButton(".", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Decimal(".")) }
            CalculatorButton(
                icon = { Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete") },
                buttonShapeId = buttonShapeId,
                buttonRatio = buttonRatio,
                isCompact = isCompact,
                modifier = Modifier.weight(1f),
                isAction = true
            ) { onAction(CalculatorAction.Delete) }
            CalculatorButton("=", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isPrimary = true) { onAction(CalculatorAction.Calculate(decimalPlaces, angleUnit)) }
        }
    }
}

@Composable
fun AdvancedPad(
    buttonShapeId: Int,
    buttonSpacing: androidx.compose.ui.unit.Dp,
    buttonRatio: Float,
    isCompact: Boolean,
    onAction: (CalculatorAction) -> Unit
) {
    val decimalPlaces = LocalDecimalPlaces.current
    val angleUnit = LocalAngleUnit.current
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(buttonSpacing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("sin", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Function("sin(")) }
            CalculatorButton("cos", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Function("cos(")) }
            CalculatorButton("tan", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Function("tan(")) }
            CalculatorButton("log", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Function("log(")) }
            CalculatorButton("ln", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Function("ln(")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("C", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Clear) }
            CalculatorButton("(", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("(")) }
            CalculatorButton(")", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator(")")) }
            CalculatorButton("^", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("^")) }
            CalculatorButton("/", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("/")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("7", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(7)) }
            CalculatorButton("8", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(8)) }
            CalculatorButton("9", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(9)) }
            CalculatorButton("√", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Function("√(")) }
            CalculatorButton("*", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("*")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("4", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(4)) }
            CalculatorButton("5", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(5)) }
            CalculatorButton("6", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(6)) }
            CalculatorButton("π", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Constant("π")) }
            CalculatorButton("-", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("-")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("1", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(1)) }
            CalculatorButton("2", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(2)) }
            CalculatorButton("3", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(3)) }
            CalculatorButton("e", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Constant("e")) }
            CalculatorButton("+", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isAction = true) { onAction(CalculatorAction.Operator("+")) }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(buttonSpacing)
        ) {
            CalculatorButton("0", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Number(0)) }
            CalculatorButton(".", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f)) { onAction(CalculatorAction.Decimal(".")) }
            CalculatorButton(
                icon = { Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete") },
                buttonShapeId = buttonShapeId,
                buttonRatio = buttonRatio,
                isCompact = isCompact,
                modifier = Modifier.weight(1f),
                isAction = true
            ) { onAction(CalculatorAction.Delete) }
            Spacer(modifier = Modifier.weight(1f))
            CalculatorButton("=", buttonShapeId = buttonShapeId, buttonRatio = buttonRatio, isCompact = isCompact, modifier = Modifier.weight(1f), isPrimary = true) { onAction(CalculatorAction.Calculate(decimalPlaces, angleUnit)) }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String = "",
    icon: @Composable (() -> Unit)? = null,
    isAction: Boolean = false,
    isPrimary: Boolean = false,
    buttonShapeId: Int = 0,
    buttonRatio: Float = 1f,
    isCompact: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val containerColor = when {
        isPrimary -> MaterialTheme.colorScheme.primary
        isAction -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    
    val contentColor = when {
        isPrimary -> MaterialTheme.colorScheme.onPrimary
        isAction -> MaterialTheme.colorScheme.onSecondaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    val shape = when (buttonShapeId) {
        1 -> RoundedCornerShape(if (isCompact) 10.dp else 16.dp)
        2 -> RoundedCornerShape(if (isCompact) 20.dp else 32.dp)
        else -> CircleShape
    }

    val haptic = LocalHapticFeedback.current
    val hapticEnabled = LocalHapticFeedbackEnabled.current

    val finalRatio = if (text == "sin" || text == "cos" || text == "tan" || text == "log" || text == "ln") {
        buttonRatio * 1.2f
    } else {
        buttonRatio
    }

    Box(
        modifier = modifier
            .aspectRatio(finalRatio)
            .clip(shape)
            .background(containerColor)
            .clickable {
                if (hapticEnabled) {
                    try {
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    } catch (e: Throwable) {
                        // Safe fallback on virtual devices, emulators, or headless servers with no haptic support
                    }
                }
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (icon != null) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Box(modifier = Modifier.size(if (isCompact) 20.dp else 24.dp)) {
                    icon()
                }
            }
        } else {
            val fontSize = if (text.length > 1) {
                if (isCompact) 13.sp else 20.sp
            } else {
                if (isCompact) 22.sp else 32.sp
            }
            Text(
                text = text,
                style = MaterialTheme.typography.titleLarge,
                fontSize = fontSize,
                color = contentColor
            )
        }
    }
}
