package com.pokertrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokertrainer.domain.models.*
import com.pokertrainer.data.StudyGroup
import com.pokertrainer.data.Opponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class ScheduleUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val scheduledSessions: List<ScheduledSession> = emptyList(),
    val suggestedSessions: List<SuggestedSession> = emptyList(),
    val studyGroups: List<StudyGroup> = emptyList(),
    val recentOpponents: List<Opponent> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ScheduledSession(
    val id: String,
    val sessionType: SessionType,
    val title: String,
    val description: String,
    val scheduledTime: LocalDateTime,
    val duration: Int, // em minutos
    val difficulty: String,
    val priority: SessionPriority,
    val category: DrillCategory,
    val estimatedImprovement: Double, // 0.0 to 1.0
    val isCompleted: Boolean = false,
    val isOptimal: Boolean = false // baseado no horário ideal do usuário
)

data class SuggestedSession(
    val id: String,
    val title: String,
    val description: String,
    val category: DrillCategory,
    val reason: String, // Por que foi sugerido
    val priority: SessionPriority,
    val estimatedDuration: Int,
    val potentialImprovement: Double,
    val basedOnWeakness: String? = null
)

enum class SessionType {
    DRILL_PRACTICE,
    CONCEPT_STUDY,
    HAND_REVIEW,
    MENTAL_GAME,
    TOURNAMENT_PREP
}

enum class SessionPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}

class ScheduleViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()
    
    init {
        loadScheduleData()
    }
    
    private fun loadScheduleData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Dados simulados - em produção viria do repository
                val scheduledSessions = generateScheduledSessions()
                val suggestedSessions = generateSuggestedSessions()
                val studyGroups = com.pokertrainer.data.SampleData.studyGroups
                val opponents = com.pokertrainer.data.SampleData.opponents
                
                _uiState.value = _uiState.value.copy(
                    scheduledSessions = scheduledSessions,
                    suggestedSessions = suggestedSessions,
                    studyGroups = studyGroups,
                    recentOpponents = opponents,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    isLoading = false
                )
            }
        }
    }
    
    fun selectDate(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        // Recarregar sessões para a data selecionada
        loadSessionsForDate(date)
    }
    
    fun scheduleSession(suggestion: SuggestedSession, scheduledTime: LocalDateTime) {
        viewModelScope.launch {
            val newSession = ScheduledSession(
                id = "scheduled_${System.currentTimeMillis()}",
                sessionType = when (suggestion.category) {
                    DrillCategory.PREFLOP -> SessionType.DRILL_PRACTICE
                    DrillCategory.POSTFLOP -> SessionType.DRILL_PRACTICE
                    DrillCategory.HAND_READING -> SessionType.HAND_REVIEW
                    DrillCategory.BANKROLL_MANAGEMENT -> SessionType.CONCEPT_STUDY
                },
                title = suggestion.title,
                description = suggestion.description,
                scheduledTime = scheduledTime,
                duration = suggestion.estimatedDuration,
                difficulty = "Adaptativo",
                priority = suggestion.priority,
                category = suggestion.category,
                estimatedImprovement = suggestion.potentialImprovement,
                isOptimal = isOptimalTime(scheduledTime)
            )
            
            val currentSessions = _uiState.value.scheduledSessions.toMutableList()
            currentSessions.add(newSession)
            
            _uiState.value = _uiState.value.copy(
                scheduledSessions = currentSessions.sortedBy { it.scheduledTime }
            )
        }
    }
    
    fun completeSession(sessionId: String) {
        val updatedSessions = _uiState.value.scheduledSessions.map { session ->
            if (session.id == sessionId) {
                session.copy(isCompleted = true)
            } else {
                session
            }
        }
        
        _uiState.value = _uiState.value.copy(scheduledSessions = updatedSessions)
    }
    
    fun removeSession(sessionId: String) {
        val updatedSessions = _uiState.value.scheduledSessions.filter { it.id != sessionId }
        _uiState.value = _uiState.value.copy(scheduledSessions = updatedSessions)
    }
    
    private fun loadSessionsForDate(date: LocalDate) {
        // Filtrar sessões para a data específica
        val allSessions = generateScheduledSessions()
        val sessionsForDate = allSessions.filter { 
            it.scheduledTime.toLocalDate() == date 
        }
        
        _uiState.value = _uiState.value.copy(scheduledSessions = sessionsForDate)
    }
    
    private fun isOptimalTime(scheduledTime: LocalDateTime): Boolean {
        // Lógica para determinar horário ótimo baseado no perfil do usuário
        val hour = scheduledTime.hour
        return hour in 19..22 // Horário ideal entre 19h-22h
    }
    
    private fun generateScheduledSessions(): List<ScheduledSession> {
        val today = LocalDate.now()
        return listOf(
            ScheduledSession(
                id = "session1",
                sessionType = SessionType.DRILL_PRACTICE,
                title = "Treino de 3-bet Ranges",
                description = "Foco em ranges de 3-bet do BTN vs aperturas de CO",
                scheduledTime = today.atTime(20, 0),
                duration = 30,
                difficulty = "Intermediário",
                priority = SessionPriority.HIGH,
                category = DrillCategory.PREFLOP,
                estimatedImprovement = 0.15,
                isOptimal = true
            ),
            ScheduledSession(
                id = "session2",
                sessionType = SessionType.CONCEPT_STUDY,
                title = "Revisão de Pot Odds",
                description = "Conceitos fundamentais de pot odds para decisões de river",
                scheduledTime = today.atTime(21, 0),
                duration = 20,
                difficulty = "Básico",
                priority = SessionPriority.MEDIUM,
                category = DrillCategory.HAND_READING,
                estimatedImprovement = 0.08
            ),
            ScheduledSession(
                id = "session3",
                sessionType = SessionType.HAND_REVIEW,
                title = "Análise de Mãos - MTT",
                description = "Review de 5 mãos críticas de torneios",
                scheduledTime = today.plusDays(1).atTime(19, 30),
                duration = 45,
                difficulty = "Avançado",
                priority = SessionPriority.HIGH,
                category = DrillCategory.POSTFLOP,
                estimatedImprovement = 0.22,
                isOptimal = true
            )
        )
    }
    
    private fun generateSuggestedSessions(): List<SuggestedSession> {
        return listOf(
            SuggestedSession(
                id = "suggest1",
                title = "Bluff Catching em River",
                description = "Desenvolva sua habilidade de identificar bluffs em situações de river",
                category = DrillCategory.HAND_READING,
                reason = "Baseado na sua fraqueza identificada em river play",
                priority = SessionPriority.CRITICAL,
                estimatedDuration = 35,
                potentialImprovement = 0.25,
                basedOnWeakness = "River bluff catching"
            ),
            SuggestedSession(
                id = "suggest2",
                title = "C-bet Sizing Strategy",
                description = "Melhore a consistência dos seus sizings de continuation bet",
                category = DrillCategory.POSTFLOP,
                reason = "Análise mostra inconsistência em c-bet sizing",
                priority = SessionPriority.HIGH,
                estimatedDuration = 25,
                potentialImprovement = 0.18,
                basedOnWeakness = "C-bet sizing"
            ),
            SuggestedSession(
                id = "suggest3",
                title = "Squeeze Play Fundamentals",
                description = "Aprenda quando e como fazer squeeze plays efetivos",
                category = DrillCategory.PREFLOP,
                reason = "Área com potencial de melhoria rápida",
                priority = SessionPriority.MEDIUM,
                estimatedDuration = 20,
                potentialImprovement = 0.12
            ),
            SuggestedSession(
                id = "suggest4",
                title = "Mental Game - Tilt Control",
                description = "Técnicas para manter compostura e tomar decisões ótimas",
                category = DrillCategory.BANKROLL_MANAGEMENT,
                reason = "Importante para consistência a longo prazo",
                priority = SessionPriority.MEDIUM,
                estimatedDuration = 30,
                potentialImprovement = 0.15
            )
        )
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
