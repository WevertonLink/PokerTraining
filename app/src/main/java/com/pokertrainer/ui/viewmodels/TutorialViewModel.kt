package com.pokertrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class TutorialUiState(
    val isFirstLaunch: Boolean = false,
    val activeTutorial: Tutorial? = null,
    val currentStep: Int = 0,
    val completedTutorials: Set<String> = emptySet(),
    val availableHints: List<Hint> = emptyList(),
    val showOnboarding: Boolean = false,
    val onboardingStep: OnboardingStep = OnboardingStep.WELCOME,
    val tutorialProgress: Map<String, Float> = emptyMap(),
    val isLoading: Boolean = false
)

data class Tutorial(
    val id: String,
    val title: String,
    val description: String,
    val category: TutorialCategory,
    val difficulty: TutorialDifficulty,
    val estimatedDuration: Int, // em minutos
    val steps: List<TutorialStep>,
    val prerequisites: List<String> = emptyList(),
    val rewards: TutorialRewards? = null,
    val isCompleted: Boolean = false,
    val progress: Float = 0f
)

data class TutorialStep(
    val id: String,
    val title: String,
    val description: String,
    val instruction: String,
    val targetElement: String? = null, // ID do elemento para highlight
    val action: TutorialAction,
    val isCompleted: Boolean = false,
    val hints: List<String> = emptyList(),
    val media: TutorialMedia? = null
)

data class TutorialRewards(
    val xp: Int,
    val badge: String? = null,
    val unlocks: List<String> = emptyList()
)

data class TutorialMedia(
    val type: MediaType,
    val url: String,
    val thumbnail: String? = null
)

data class Hint(
    val id: String,
    val title: String,
    val message: String,
    val targetScreen: String,
    val priority: HintPriority,
    val trigger: HintTrigger,
    val isRead: Boolean = false,
    val expiresAt: Long? = null
)

enum class TutorialCategory {
    ONBOARDING,
    BASIC_FEATURES,
    ADVANCED_FEATURES,
    GAMIFICATION,
    ANALYTICS,
    PRACTICE,
    SOCIAL
}

enum class TutorialDifficulty {
    BEGINNER,
    INTERMEDIATE,
    ADVANCED,
    EXPERT
}

enum class TutorialAction {
    TAP,
    SWIPE,
    SCROLL,
    FILL_FORM,
    NAVIGATION,
    WAIT,
    OBSERVE
}

enum class MediaType {
    IMAGE,
    GIF,
    VIDEO,
    LOTTIE
}

enum class OnboardingStep {
    WELCOME,
    PROFILE_SETUP,
    FEATURES_OVERVIEW,
    FIRST_TRAINING,
    GAMIFICATION_INTRO,
    NOTIFICATIONS_SETUP,
    COMPLETE
}

enum class HintPriority {
    LOW,
    MEDIUM,
    HIGH,
    CRITICAL
}

enum class HintTrigger {
    SCREEN_VISIT,
    TIME_BASED,
    ACTION_BASED,
    FEATURE_DISCOVERY
}

class TutorialViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TutorialUiState())
    val uiState: StateFlow<TutorialUiState> = _uiState.asStateFlow()

    init {
        checkFirstLaunch()
        loadTutorialProgress()
        generateContextualHints()
    }

    private fun checkFirstLaunch() {
        viewModelScope.launch {
            // Simulate checking if this is first launch
            val isFirstLaunch = true // TODO: Get from SharedPreferences
            
            _uiState.value = _uiState.value.copy(
                isFirstLaunch = isFirstLaunch,
                showOnboarding = isFirstLaunch
            )
        }
    }

    private fun loadTutorialProgress() {
        viewModelScope.launch {
            val completedTutorials = setOf("onboarding_welcome") // TODO: Load from storage
            val tutorialProgress = mapOf(
                "basic_navigation" to 0.6f,
                "practice_fundamentals" to 0.3f,
                "gamification_basics" to 1.0f
            )
            
            _uiState.value = _uiState.value.copy(
                completedTutorials = completedTutorials,
                tutorialProgress = tutorialProgress
            )
        }
    }

    fun startTutorial(tutorialId: String) {
        viewModelScope.launch {
            val tutorial = getTutorialById(tutorialId)
            
            _uiState.value = _uiState.value.copy(
                activeTutorial = tutorial,
                currentStep = 0
            )
        }
    }

    fun nextTutorialStep() {
        viewModelScope.launch {
            val currentTutorial = _uiState.value.activeTutorial ?: return@launch
            val currentStep = _uiState.value.currentStep
            
            if (currentStep < currentTutorial.steps.size - 1) {
                _uiState.value = _uiState.value.copy(
                    currentStep = currentStep + 1
                )
            } else {
                completeTutorial(currentTutorial.id)
            }
        }
    }

    fun previousTutorialStep() {
        viewModelScope.launch {
            val currentStep = _uiState.value.currentStep
            
            if (currentStep > 0) {
                _uiState.value = _uiState.value.copy(
                    currentStep = currentStep - 1
                )
            }
        }
    }

    fun skipTutorial() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                activeTutorial = null,
                currentStep = 0
            )
        }
    }

    private fun completeTutorial(tutorialId: String) {
        viewModelScope.launch {
            val updatedCompleted = _uiState.value.completedTutorials + tutorialId
            val tutorial = _uiState.value.activeTutorial
            
            _uiState.value = _uiState.value.copy(
                completedTutorials = updatedCompleted,
                activeTutorial = null,
                currentStep = 0
            )
            
            // Award rewards if any
            tutorial?.rewards?.let { rewards ->
                // TODO: Award XP, badges, etc.
            }
        }
    }

    fun startOnboarding() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                showOnboarding = true,
                onboardingStep = OnboardingStep.WELCOME
            )
        }
    }

    fun nextOnboardingStep() {
        viewModelScope.launch {
            val currentStep = _uiState.value.onboardingStep
            val nextStep = when (currentStep) {
                OnboardingStep.WELCOME -> OnboardingStep.PROFILE_SETUP
                OnboardingStep.PROFILE_SETUP -> OnboardingStep.FEATURES_OVERVIEW
                OnboardingStep.FEATURES_OVERVIEW -> OnboardingStep.FIRST_TRAINING
                OnboardingStep.FIRST_TRAINING -> OnboardingStep.GAMIFICATION_INTRO
                OnboardingStep.GAMIFICATION_INTRO -> OnboardingStep.NOTIFICATIONS_SETUP
                OnboardingStep.NOTIFICATIONS_SETUP -> OnboardingStep.COMPLETE
                OnboardingStep.COMPLETE -> OnboardingStep.COMPLETE
            }
            
            _uiState.value = _uiState.value.copy(
                onboardingStep = nextStep
            )
            
            if (nextStep == OnboardingStep.COMPLETE) {
                completeOnboarding()
            }
        }
    }

    fun skipOnboarding() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                showOnboarding = false,
                isFirstLaunch = false
            )
            
            // TODO: Save to SharedPreferences that onboarding was skipped
        }
    }

    private fun completeOnboarding() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                showOnboarding = false,
                isFirstLaunch = false,
                completedTutorials = _uiState.value.completedTutorials + "onboarding_complete"
            )
            
            // TODO: Save completion to SharedPreferences
            delay(500)
            
            // Show first contextual hint
            generatePostOnboardingHints()
        }
    }

    fun markHintAsRead(hintId: String) {
        viewModelScope.launch {
            val updatedHints = _uiState.value.availableHints.map { hint ->
                if (hint.id == hintId) hint.copy(isRead = true) else hint
            }
            
            _uiState.value = _uiState.value.copy(
                availableHints = updatedHints
            )
        }
    }

    fun dismissHint(hintId: String) {
        viewModelScope.launch {
            val updatedHints = _uiState.value.availableHints.filter { it.id != hintId }
            
            _uiState.value = _uiState.value.copy(
                availableHints = updatedHints
            )
        }
    }

    private fun generateContextualHints() {
        viewModelScope.launch {
            val hints = listOf(
                Hint(
                    id = "practice_suggestion",
                    title = "💡 Dica de Prática",
                    message = "Comece com drills de pré-flop para dominar os fundamentos!",
                    targetScreen = "practice_home",
                    priority = HintPriority.MEDIUM,
                    trigger = HintTrigger.SCREEN_VISIT
                ),
                Hint(
                    id = "gamification_discovery",
                    title = "🎮 Explore a Gamificação",
                    message = "Confira seu progresso e conquistas na aba Gamificação!",
                    targetScreen = "home",
                    priority = HintPriority.HIGH,
                    trigger = HintTrigger.FEATURE_DISCOVERY
                ),
                Hint(
                    id = "notifications_setup",
                    title = "🔔 Configure Notificações",
                    message = "Ative notificações para não perder suas sequências de treino!",
                    targetScreen = "notifications",
                    priority = HintPriority.MEDIUM,
                    trigger = HintTrigger.TIME_BASED
                )
            )
            
            _uiState.value = _uiState.value.copy(
                availableHints = hints
            )
        }
    }

    private fun generatePostOnboardingHints() {
        viewModelScope.launch {
            val postOnboardingHints = listOf(
                Hint(
                    id = "first_practice",
                    title = "🎯 Primeira Prática",
                    message = "Que tal começar com um drill rápido de 5 minutos?",
                    targetScreen = "practice_home",
                    priority = HintPriority.HIGH,
                    trigger = HintTrigger.ACTION_BASED
                ),
                Hint(
                    id = "schedule_session",
                    title = "📅 Agende um Treino",
                    message = "Use a IA para sugerir o melhor horário para sua próxima sessão!",
                    targetScreen = "schedule",
                    priority = HintPriority.MEDIUM,
                    trigger = HintTrigger.FEATURE_DISCOVERY
                )
            )
            
            val currentHints = _uiState.value.availableHints
            _uiState.value = _uiState.value.copy(
                availableHints = currentHints + postOnboardingHints
            )
        }
    }

    fun getAvailableTutorials(): List<Tutorial> {
        return listOf(
            Tutorial(
                id = "basic_navigation",
                title = "Navegação Básica",
                description = "Aprenda a navegar pelo aplicativo",
                category = TutorialCategory.BASIC_FEATURES,
                difficulty = TutorialDifficulty.BEGINNER,
                estimatedDuration = 3,
                steps = createBasicNavigationSteps(),
                rewards = TutorialRewards(xp = 100, badge = "Navigator")
            ),
            Tutorial(
                id = "practice_fundamentals",
                title = "Fundamentos da Prática",
                description = "Como usar os drills de prática efetivamente",
                category = TutorialCategory.PRACTICE,
                difficulty = TutorialDifficulty.BEGINNER,
                estimatedDuration = 8,
                steps = createPracticeFundamentalsSteps(),
                rewards = TutorialRewards(xp = 250, badge = "Student")
            ),
            Tutorial(
                id = "gamification_advanced",
                title = "Gamificação Avançada",
                description = "Maximize seus XP e conquistas",
                category = TutorialCategory.GAMIFICATION,
                difficulty = TutorialDifficulty.INTERMEDIATE,
                estimatedDuration = 12,
                steps = createGamificationAdvancedSteps(),
                prerequisites = listOf("basic_navigation", "practice_fundamentals"),
                rewards = TutorialRewards(xp = 500, badge = "Gamification Master", unlocks = listOf("advanced_challenges"))
            ),
            Tutorial(
                id = "analytics_deep_dive",
                title = "Análise Profunda de Performance",
                description = "Entenda e use as métricas avançadas",
                category = TutorialCategory.ANALYTICS,
                difficulty = TutorialDifficulty.ADVANCED,
                estimatedDuration = 15,
                steps = createAnalyticsDeepDiveSteps(),
                prerequisites = listOf("practice_fundamentals"),
                rewards = TutorialRewards(xp = 750, badge = "Data Analyst")
            )
        )
    }

    private fun getTutorialById(tutorialId: String): Tutorial? {
        return getAvailableTutorials().find { it.id == tutorialId }
    }

    private fun createBasicNavigationSteps(): List<TutorialStep> {
        return listOf(
            TutorialStep(
                id = "welcome",
                title = "Bem-vindo!",
                description = "Vamos começar explorando o PokerTrainer",
                instruction = "Toque em 'Próximo' para continuar",
                action = TutorialAction.TAP
            ),
            TutorialStep(
                id = "home_screen",
                title = "Tela Inicial",
                description = "Esta é sua tela principal com resumo de atividades",
                instruction = "Observe os cards de estatísticas e atividade",
                action = TutorialAction.OBSERVE,
                targetElement = "home_cards"
            ),
            TutorialStep(
                id = "bottom_navigation",
                title = "Navegação",
                description = "Use a barra inferior para navegar entre seções",
                instruction = "Toque na aba 'Agenda'",
                action = TutorialAction.TAP,
                targetElement = "schedule_tab"
            ),
            TutorialStep(
                id = "notifications",
                title = "Notificações",
                description = "Acesse suas notificações pelo sino no topo",
                instruction = "Toque no ícone de notificação",
                action = TutorialAction.TAP,
                targetElement = "notification_bell"
            )
        )
    }

    private fun createPracticeFundamentalsSteps(): List<TutorialStep> {
        return listOf(
            TutorialStep(
                id = "practice_intro",
                title = "Área de Prática",
                description = "Aqui você encontra drills organizados por categoria",
                instruction = "Navegue até a área de prática",
                action = TutorialAction.NAVIGATION
            ),
            TutorialStep(
                id = "drill_selection",
                title = "Escolhendo um Drill",
                description = "Drills são exercícios focados em habilidades específicas",
                instruction = "Selecione um drill de pré-flop",
                action = TutorialAction.TAP,
                targetElement = "preflop_drill"
            ),
            TutorialStep(
                id = "drill_execution",
                title = "Executando o Drill",
                description = "Siga as instruções e pratique as situações apresentadas",
                instruction = "Complete pelo menos 3 cenários",
                action = TutorialAction.FILL_FORM
            ),
            TutorialStep(
                id = "results_review",
                title = "Revisão de Resultados",
                description = "Analise seu desempenho e áreas de melhoria",
                instruction = "Revise os resultados do drill",
                action = TutorialAction.OBSERVE
            )
        )
    }

    private fun createGamificationAdvancedSteps(): List<TutorialStep> {
        return listOf(
            TutorialStep(
                id = "xp_system",
                title = "Sistema de XP",
                description = "Ganhe XP completando atividades e desafios",
                instruction = "Observe sua barra de XP atual",
                action = TutorialAction.OBSERVE,
                targetElement = "xp_bar"
            ),
            TutorialStep(
                id = "achievements",
                title = "Conquistas",
                description = "Desbloqueie conquistas cumprindo objetivos específicos",
                instruction = "Explore a seção de conquistas",
                action = TutorialAction.TAP,
                targetElement = "achievements_tab"
            ),
            TutorialStep(
                id = "daily_challenges",
                title = "Desafios Diários",
                description = "Complete desafios diários para XP extra",
                instruction = "Aceite um desafio diário",
                action = TutorialAction.TAP
            ),
            TutorialStep(
                id = "leaderboard",
                title = "Ranking Global",
                description = "Compare seu progresso com outros jogadores",
                instruction = "Veja sua posição no ranking",
                action = TutorialAction.TAP,
                targetElement = "leaderboard"
            )
        )
    }

    private fun createAnalyticsDeepDiveSteps(): List<TutorialStep> {
        return listOf(
            TutorialStep(
                id = "performance_metrics",
                title = "Métricas de Performance",
                description = "Entenda suas estatísticas principais",
                instruction = "Acesse a tela de estatísticas",
                action = TutorialAction.NAVIGATION
            ),
            TutorialStep(
                id = "trend_analysis",
                title = "Análise de Tendências",
                description = "Observe como sua performance evolui ao longo do tempo",
                instruction = "Examine os gráficos de tendência",
                action = TutorialAction.OBSERVE,
                targetElement = "trend_charts"
            ),
            TutorialStep(
                id = "weak_spots",
                title = "Identificando Pontos Fracos",
                description = "Use a análise para focar nos pontos que precisam de melhoria",
                instruction = "Identifique suas áreas de menor performance",
                action = TutorialAction.OBSERVE
            ),
            TutorialStep(
                id = "recommendations",
                title = "Recomendações Personalizadas",
                description = "A IA sugere treinos baseados em sua análise",
                instruction = "Revise as recomendações geradas",
                action = TutorialAction.OBSERVE,
                targetElement = "ai_recommendations"
            )
        )
    }
}
