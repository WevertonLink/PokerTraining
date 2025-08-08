package com.pokertrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pokertrainer.data.repository.TrainingRepositoryImpl
import com.pokertrainer.domain.models.*
import com.pokertrainer.domain.usecases.GeneratePerformanceAnalysisUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para tela de análise de performance
 */
class PerformanceAnalysisViewModel(
    private val generateAnalysisUseCase: GeneratePerformanceAnalysisUseCase = GeneratePerformanceAnalysisUseCase(
        TrainingRepositoryImpl()
    )
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(PerformanceAnalysisUiState())
    val uiState: StateFlow<PerformanceAnalysisUiState> = _uiState.asStateFlow()
    
    init {
        loadPerformanceAnalysis()
    }
    
    fun loadPerformanceAnalysis(period: AnalysisPeriod = AnalysisPeriod.LAST_30_DAYS) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            try {
                // Por enquanto, usar dados de exemplo
                // Em produção: generateAnalysisUseCase("player_1", period)
                val analysis = com.pokertrainer.data.SampleData.sampleAnalysis
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    analysis = analysis,
                    selectedPeriod = period
                )
            } catch (error: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = error.message ?: "Erro desconhecido"
                )
            }
        }
    }
    
    fun changePeriod(period: AnalysisPeriod) {
        if (period != _uiState.value.selectedPeriod) {
            loadPerformanceAnalysis(period)
        }
    }
    
    fun dismissError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun toggleCategoryExpansion(category: DrillCategory) {
        val currentExpanded = _uiState.value.expandedCategories
        val newExpanded = if (currentExpanded.contains(category)) {
            currentExpanded - category
        } else {
            currentExpanded + category
        }
        
        _uiState.value = _uiState.value.copy(expandedCategories = newExpanded)
    }
    
    fun markRecommendationAsViewed(recommendationId: String) {
        val current = _uiState.value.analysis ?: return
        val updatedRecommendations = current.recommendations.map { rec ->
            if (rec.id == recommendationId) {
                // Marcar como visualizada (poderia ser persistido)
                rec
            } else rec
        }
        
        _uiState.value = _uiState.value.copy(
            analysis = current.copy(recommendations = updatedRecommendations)
        )
    }
    
    fun selectTab(tab: AnalysisTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }
}

/**
 * Estado da UI da análise de performance
 */
data class PerformanceAnalysisUiState(
    val isLoading: Boolean = false,
    val analysis: PerformanceAnalysis? = null,
    val selectedPeriod: AnalysisPeriod = AnalysisPeriod.LAST_30_DAYS,
    val selectedTab: AnalysisTab = AnalysisTab.OVERVIEW,
    val expandedCategories: Set<DrillCategory> = emptySet(),
    val error: String? = null
)

/**
 * Abas disponíveis na análise
 */
enum class AnalysisTab {
    OVERVIEW,      // Visão geral
    CATEGORIES,    // Performance por categoria
    WEAKNESSES,    // Áreas de fraqueza
    RECOMMENDATIONS, // Recomendações
    PROGRESS       // Progresso ao longo do tempo
}
