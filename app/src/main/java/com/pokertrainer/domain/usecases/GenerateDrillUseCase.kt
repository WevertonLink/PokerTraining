package com.pokertrainer.domain.usecases

import com.pokertrainer.data.repository.TrainingRepository
import com.pokertrainer.domain.models.*

class GenerateDrillUseCase(
    private val repository: TrainingRepository
) {
    suspend operator fun invoke(drillId: Int): TrainingDrill {
        return repository.getDrillById(drillId) ?: throw DrillNotFoundException(
            "Drill with id $drillId not found"
        )
    }

    suspend fun generateRandomScenario(category: DrillCategory): DrillScenario {
        return when (category) {
            DrillCategory.PREFLOP -> generatePreflopScenario()
            DrillCategory.POSTFLOP -> generatePostflopScenario()
            DrillCategory.HAND_READING -> generateHandReadingScenario()
            DrillCategory.BANKROLL_MANAGEMENT -> generateBankrollScenario()
        }
    }

    private fun generatePreflopScenario(): DrillScenario {
        val position = Position.entries.random()
        val action = ActionType.entries.random()

        return DrillScenario(
            position = position,
            action = action,
            correctRanges = repository.getStandardRange(position, action),
            description = "Situação ${action.name.lowercase().replace("_", " ")} na posição ${position.name}"
        )
    }

    private fun generatePostflopScenario(): DrillScenario {
        // Implementação para cenários pós-flop
        return DrillScenario(
            position = Position.BTN,
            action = ActionType.UNOPENED,
            correctRanges = listOf("AA", "KK", "QQ"),
            description = "Cenário pós-flop complexo"
        )
    }

    private fun generateHandReadingScenario(): DrillScenario {
        // Implementação para leitura de mãos
        return DrillScenario(
            position = Position.BB,
            action = ActionType.THREE_BET,
            correctRanges = listOf("AA", "KK", "AKs"),
            description = "Identifique o range do oponente"
        )
    }

    private fun generateBankrollScenario(): DrillScenario {
        // Implementação para gestão de bankroll
        return DrillScenario(
            position = Position.UTG,
            action = ActionType.UNOPENED,
            correctRanges = emptyList(),
            description = "Decisão de gestão de bankroll"
        )
    }
}

class DrillNotFoundException(message: String) : Exception(message)
