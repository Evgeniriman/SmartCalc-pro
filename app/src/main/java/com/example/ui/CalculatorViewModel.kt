package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CalculationHistory
import com.example.data.HistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CalculatorUiState(
    val expression: String = "",
    val result: String = "",
    val isError: Boolean = false
)

sealed class CalculatorAction {
    data class Number(val number: Int) : CalculatorAction()
    data class Operator(val operator: String) : CalculatorAction()
    data class Decimal(val decimal: String) : CalculatorAction()
    data class Function(val function: String) : CalculatorAction()
    data class Constant(val constant: String) : CalculatorAction()
    object Clear : CalculatorAction()
    object Delete : CalculatorAction()
    data class Calculate(val decimalPlaces: Int = 0, val angleUnit: Int = 0) : CalculatorAction()
    object ClearHistory : CalculatorAction()
}

class CalculatorViewModel(private val repository: HistoryRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(CalculatorUiState())
    val uiState: StateFlow<CalculatorUiState> = _uiState.asStateFlow()

    val history: StateFlow<List<CalculationHistory>> = repository.allHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onAction(action: CalculatorAction) {
        when (action) {
            is CalculatorAction.Number -> appendNumber(action.number)
            is CalculatorAction.Operator -> appendOperator(action.operator)
            is CalculatorAction.Decimal -> appendDecimal(action.decimal)
            is CalculatorAction.Function -> appendFunction(action.function)
            is CalculatorAction.Constant -> appendConstant(action.constant)
            CalculatorAction.Clear -> clear()
            CalculatorAction.Delete -> delete()
            is CalculatorAction.Calculate -> calculate(action.decimalPlaces, action.angleUnit)
            CalculatorAction.ClearHistory -> clearHistory()
        }
    }

    private fun appendFunction(function: String) {
        _uiState.update { it.copy(expression = it.expression + function, isError = false, result = "") }
    }

    private fun appendConstant(constant: String) {
        _uiState.update { it.copy(expression = it.expression + constant, isError = false, result = "") }
    }

    private fun appendNumber(number: Int) {
        _uiState.update { it.copy(expression = it.expression + number, isError = false, result = "") }
    }

    private fun appendDecimal(decimal: String) {
        val currentExpression = _uiState.value.expression
        // Basic check to see if we can append decimal
        if (currentExpression.isNotEmpty() && !currentExpression.last().toString().matches(Regex("[+\\-*/.]"))) {
           _uiState.update { it.copy(expression = it.expression + decimal, isError = false, result = "") }
        } else if (currentExpression.isEmpty()) {
            _uiState.update { it.copy(expression = "0.", isError = false, result = "") }
        }
    }

    private fun appendOperator(operator: String) {
        val currentExpression = _uiState.value.expression
        if (currentExpression.isNotEmpty() && !currentExpression.last().toString().matches(Regex("[+\\-*/.^]"))) {
            _uiState.update { it.copy(expression = it.expression + operator, isError = false, result = "") }
        } else if (operator == "(" || operator == ")") {
            _uiState.update { it.copy(expression = it.expression + operator, isError = false, result = "") }
        } else if (currentExpression.isEmpty() && (operator == "-" || operator == "+")) {
             _uiState.update { it.copy(expression = it.expression + operator, isError = false, result = "") }
        }
    }

    private fun clear() {
        _uiState.update { CalculatorUiState() }
    }

    private fun delete() {
        val currentExpression = _uiState.value.expression
        if (currentExpression.isNotEmpty()) {
            _uiState.update { it.copy(expression = currentExpression.dropLast(1), isError = false, result = "") }
        }
    }

    private fun calculate(decimalPlaces: Int = 0, angleUnit: Int = 0) {
        val currentExpression = _uiState.value.expression
        if (currentExpression.isEmpty()) return

        try {
            val result = evaluateExpression(currentExpression, angleUnit = angleUnit)
            val formattedResult = if (decimalPlaces == 0) {
                if (result % 1.0 == 0.0) {
                    result.toLong().toString()
                } else {
                    result.toString()
                }
            } else {
                val places = when (decimalPlaces) {
                    1 -> 2
                    2 -> 4
                    3 -> 6
                    else -> 2
                }
                val formatStr = "%.${places}f"
                String.format(java.util.Locale.US, formatStr, result)
            }
            _uiState.update { it.copy(result = formattedResult, isError = false) }
            
            viewModelScope.launch {
                repository.insert(CalculationHistory(expression = currentExpression, result = formattedResult))
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(result = "Error", isError = true) }
        }
    }
    
    private fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    private fun evaluateExpression(expression: String, angleUnit: Int = 0): Double {
        return eval(expression, angleUnit = angleUnit)
    }
    
    private fun eval(str: String, angleUnit: Int = 0): Double {
        return object : Any() {
            var pos = -1
            var ch = 0

            fun nextChar() {
                ch = if (++pos < str.length) str[pos].code else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.code) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
                nextChar()
                val x = parseExpression()
                if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
                return x
            }

            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.code)) x += parseTerm()
                    else if (eat('-'.code)) x -= parseTerm()
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.code)) x *= parseFactor()
                    else if (eat('/'.code)) x /= parseFactor()
                    else return x
                }
            }

            fun parseFactor(): Double {
                if (eat('+'.code)) return parseFactor()
                if (eat('-'.code)) return -parseFactor()

                var x: Double
                val startPos = pos
                if (eat('('.code)) {
                    x = parseExpression()
                    eat(')'.code)
                } else if (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) {
                    while (ch >= '0'.code && ch <= '9'.code || ch == '.'.code) nextChar()
                    x = str.substring(startPos, pos).toDouble()
                } else if (ch >= 'a'.code && ch <= 'z'.code || ch == 'π'.code || ch == '√'.code || ch == '^'.code) {
                    while (ch >= 'a'.code && ch <= 'z'.code || ch == 'π'.code || ch == '√'.code || ch == '^'.code) nextChar()
                    val func = str.substring(startPos, pos)
                    x = if (func == "π") Math.PI else if (func == "e") Math.E else parseFactor()
                    if (func == "sqrt" || func == "√") x = Math.sqrt(x)
                    else if (func == "sin") {
                        val angle = if (angleUnit == 0) Math.toRadians(x) else x
                        x = Math.sin(angle)
                    }
                    else if (func == "cos") {
                        val angle = if (angleUnit == 0) Math.toRadians(x) else x
                        x = Math.cos(angle)
                    }
                    else if (func == "tan") {
                        val angle = if (angleUnit == 0) Math.toRadians(x) else x
                        x = Math.tan(angle)
                    }
                    else if (func == "log") x = Math.log10(x)
                    else if (func == "ln") x = Math.log(x)
                    else if (func != "π" && func != "e") throw RuntimeException("Unknown function: $func")
                } else {
                    throw RuntimeException("Unexpected: " + ch.toChar())
                }
                
                if (eat('^'.code)) x = Math.pow(x, parseFactor())
                
                return x
            }
        }.parse()
    }
}

class CalculatorViewModelFactory(private val repository: HistoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CalculatorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalculatorViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
