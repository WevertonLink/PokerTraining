package com.pokertrainer.domain.models

data class TrainingDrill(
    val id: Int,
    val title: String,
    val description: String,
    val category: DrillCategory,
    val difficulty: Int, // 1-5
    val duration: Int, // minutos
    val scenarios: List<DrillScenario>,
    val completed: Boolean = false
)

enum class DrillCategory {
    PREFLOP, POSTFLOP, HAND_READING, BANKROLL_MANAGEMENT
}

data class DrillScenario(
    val position: Position,
    val action: ActionType,
    val correctRanges: List<String>, // Lista de combos (ex: "AKs", "QQ+")
    val description: String = ""
)

enum class Position {
    UTG, MP, CO, BTN, SB, BB
}

enum class ActionType {
    UNOPENED, FOLDED_TO, ONE_LIMP, ONE_RAISE, THREE_BET, FOUR_BET_PLUS
}

data class PokerHand(
    val id: String,
    val position: Position,
    val holeCards: List<String>, // ["As", "Kd"]
    val preflop: List<Action>,
    val flop: BoardState? = null,
    val turn: BoardState? = null,
    val river: BoardState? = null
)

data class Action(
    val player: String,
    val actionType: String, // "fold", "call", "raise", "check"
    val amount: Double = 0.0
)

data class BoardState(
    val cards: List<String>, // ["Ah", "Kd", "Qs"]
    val actions: List<Action>
)

data class TrainingSession(
    val id: String,
    val drillId: Int,
    val startTime: Long,
    val endTime: Long? = null,
    val scenarios: List<SessionScenario>,
    val totalCorrect: Int = 0,
    val totalQuestions: Int = 0
)

data class SessionScenario(
    val scenario: DrillScenario,
    val userRanges: List<String>,
    val correctRanges: List<String>,
    val timeSpent: Long,
    val isCorrect: Boolean
)
