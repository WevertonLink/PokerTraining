package com.pokertrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.pokertrainer.data.repository.TrainingRepositoryImpl
import com.pokertrainer.domain.models.*
import com.pokertrainer.domain.usecases.EvaluateDecisionUseCase
import com.pokertrainer.domain.usecases.GenerateDrillUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PreflopDrillViewModel(
    private val drillId: Int,
    private val generateDrillUseCase: GenerateDrillUseCase = GenerateDrillUseCase(TrainingRepositoryImpl()),
    private val evaluateDecisionUseCase: EvaluateDecisionUseCase = EvaluateDecisionUseCase(TrainingRepositoryImpl())
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PreflopDrillUiState())
    val uiState: StateFlow<PreflopDrillUiState> = _uiState.asStateFlow()
    
    private val _events = MutableSharedFlow<PreflopDrillEvent>()
    val events: SharedFlow<PreflopDrillEvent> = _events.asSharedFlow()
    
    private var timerJob: Job? = null
    
    init {
        loadDrill()
    }
    
    private fun loadDrill() {
        viewModelScope.launch {
            try {
                val drill = generateDrillUseCase(drillId)
                _uiState.update { currentState ->
                    currentState.copy(
                        drill = drill,
                        currentScenario = drill.scenarios.first(),
                        totalQuestions = drill.scenarios.size,
                        timeRemaining = drill.duration * 60 * 1000L,
                        isLoading = false
                    )
                }
                startTimer()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }
    
    private fun startTimer() {
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_uiState.value.timeRemaining > 0) {
                delay(1000)
                _uiState.update { 
                    it.copy(timeRemaining = it.timeRemaining - 1000)
                }
            }
            _events.emit(PreflopDrillEvent.TimeExpired)
        }
    }
    
    fun toggleCombo(combo: String) {
        _uiState.update { currentState ->
            val updatedCombos = if (combo in currentState.selectedCombos) {
                currentState.selectedCombos - combo
            } else {
                currentState.selectedCombos + combo
            }
            currentState.copy(selectedCombos = updatedCombos)
        }
    }
    
    fun resetSelection() {
        _uiState.update { it.copy(selectedCombos = emptySet()) }
    }
    
    fun submitAnswer() {
        val currentState = _uiState.value
        val result = evaluateDecisionUseCase(
            userSelection = currentState.selectedCombos,
            correctRanges = currentState.currentScenario.correctRanges
        )
        
        _uiState.update { state ->
            state.copy(
                lastResult = result,
                showFeedback = true,
                correctAnswers = if (result.isCorrect) state.correctAnswers + 1 else state.correctAnswers
            )
        }
    }
    
    fun nextQuestion() {
        val currentState = _uiState.value
        val nextQuestionIndex = currentState.currentQuestion + 1
        
        if (nextQuestionIndex < currentState.totalQuestions) {
            _uiState.update { state ->
                state.copy(
                    currentQuestion = nextQuestionIndex,
                    currentScenario = state.drill.scenarios[nextQuestionIndex],
                    selectedCombos = emptySet(),
                    showFeedback = false,
                    lastResult = null
                )
            }
        } else {
            completeDrill()
        }
    }
    
    private fun completeDrill() {
        timerJob?.cancel()
        val accuracy = (_uiState.value.correctAnswers.toDouble() / _uiState.value.totalQuestions) * 100
        viewModelScope.launch {
            _events.emit(PreflopDrillEvent.DrillComplete(accuracy.toInt()))
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

data class PreflopDrillUiState(
    val drill: TrainingDrill = TrainingDrill(
        id = 0,
        title = "",
        description = "",
        category = DrillCategory.PREFLOP,
        difficulty = 1,
        duration = 15,
        scenarios = emptyList()
    ),
    val currentQuestion: Int = 0,
    val totalQuestions: Int = 0,
    val currentScenario: DrillScenario = DrillScenario(
        position = Position.UTG,
        action = ActionType.UNOPENED,
        correctRanges = emptyList()
    ),
    val selectedCombos: Set<String> = emptySet(),
    val timeRemaining: Long = 0L,
    val showFeedback: Boolean = false,
    val lastResult: com.pokertrainer.domain.usecases.DecisionResult? = null,
    val correctAnswers: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

sealed class PreflopDrillEvent {
    data class DrillComplete(val accuracy: Int) : PreflopDrillEvent()
    object TimeExpired : PreflopDrillEvent()
}

class PreflopDrillViewModelFactory(
    private val drillId: Int
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreflopDrillViewModel::class.java)) {
            return PreflopDrillViewModel(drillId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
