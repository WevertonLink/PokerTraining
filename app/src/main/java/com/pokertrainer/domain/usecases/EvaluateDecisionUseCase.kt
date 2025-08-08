package com.pokertrainer.domain.usecases

import com.pokertrainer.data.repository.TrainingRepository

class EvaluateDecisionUseCase(
    private val repository: TrainingRepository
) {

    operator fun invoke(
        userSelection: Set<String>,
        correctRanges: List<String>
    ): DecisionResult {
        val correctSet = correctRanges.toSet()
        val userSet = userSelection

        val correctSelections = userSet.intersect(correctSet).size
        val incorrectSelections = userSet.minus(correctSet).size
        val missedSelections = correctSet.minus(userSet).size

        val accuracy = if (correctSet.isEmpty()) {
            if (userSet.isEmpty()) 100.0 else 0.0
        } else {
            (correctSelections.toDouble() / correctSet.size) * 100.0
        }

        val precision = if (userSet.isEmpty()) {
            0.0
        } else {
            (correctSelections.toDouble() / userSet.size) * 100.0
        }

        return DecisionResult(
            accuracy = accuracy,
            precision = precision,
            correctSelections = correctSelections,
            incorrectSelections = incorrectSelections,
            missedSelections = missedSelections,
            isCorrect = accuracy >= 80.0 && precision >= 80.0,
            feedback = generateFeedback(accuracy, precision, incorrectSelections, missedSelections)
        )
    }

    private fun generateFeedback(
        accuracy: Double,
        precision: Double,
        incorrectSelections: Int,
        missedSelections: Int
    ): String {
        return when {
            accuracy >= 90.0 && precision >= 90.0 -> "Excelente! Seleção quase perfeita."
            accuracy >= 80.0 && precision >= 80.0 -> "Muito bom! Algumas pequenas melhorias possíveis."
            accuracy >= 70.0 -> "Bom resultado, mas você perdeu algumas mãos importantes."
            precision >= 70.0 -> "Você selecionou mãos corretas, mas incluiu algumas que não deveria."
            incorrectSelections > missedSelections -> "Cuidado com as mãos extras. Seja mais seletivo."
            missedSelections > incorrectSelections -> "Você foi muito conservador. Inclua mais mãos no range."
            else -> "Continue praticando. Revise os conceitos fundamentais."
        }
    }
}

data class DecisionResult(
    val accuracy: Double,
    val precision: Double,
    val correctSelections: Int,
    val incorrectSelections: Int,
    val missedSelections: Int,
    val isCorrect: Boolean,
    val feedback: String
)
