package com.pokertrainer.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex

data class HandScenario(
    val id: Int,
    val position: String,
    val cards: String,
    val action: String,
    val villain: String,
    val pot: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String
)

@Composable
fun TrainingScreen(
    onBackPressed: () -> Unit = {}
) {
    var currentScenario by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    var showResult by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    
    val scenarios = remember {
        listOf(
            HandScenario(
                id = 1,
                position = "BTN",
                cards = "A♠ K♦",
                action = "UTG raises 3BB",
                villain = "TAG (22/18)",
                pot = "4.5BB",
                question = "Qual sua ação?",
                options = listOf("Fold", "Call", "3-bet", "All-in"),
                correctAnswer = 2,
                explanation = "AKo no BTN contra raise de UTG é um 3-bet claro para valor e isolamento."
            ),
            HandScenario(
                id = 2,
                position = "BB",
                cards = "Q♥ J♠",
                action = "CO raises 2.5BB, BTN calls",
                villain = "LAG (28/24)",
                pot = "6BB",
                question = "Qual sua ação?",
                options = listOf("Fold", "Call", "3-bet", "Check"),
                correctAnswer = 1,
                explanation = "QJs no BB com 2 players é um call com boa frequência, especialmente com posição pós-flop."
            )
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackPressed) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = "Treinamento Pre-flop",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    text = "Cenário ${currentScenario + 1} de ${scenarios.size} • Score: $score/${currentScenario}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
        }
        
        if (currentScenario < scenarios.size) {
            val scenario = scenarios[currentScenario]
            
            // Progress Bar
            LinearProgressIndicator(
                progress = { (currentScenario + 1).toFloat() / scenarios.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = colorFromHex(Tokens.Primary),
                trackColor = colorFromHex(Tokens.Border)
            )
            
            Spacer(Modifier.height(Tokens.SectionSpacing))
            
            // Scenario Card
            ScenarioCard(scenario)
            
            Spacer(Modifier.height(Tokens.SectionSpacing))
            
            // Options
            if (!showResult) {
                scenario.options.forEachIndexed { index, option ->
                    OptionButton(
                        text = option,
                        isSelected = selectedAnswer == index,
                        onClick = { selectedAnswer = index }
                    )
                    Spacer(Modifier.height(Tokens.ItemSpacing))
                }
                
                Spacer(Modifier.height(Tokens.SectionSpacing))
                
                // Submit Button
                Button(
                    onClick = {
                        showResult = true
                        if (selectedAnswer == scenario.correctAnswer) {
                            score++
                        }
                    },
                    enabled = selectedAnswer != null,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorFromHex(Tokens.Primary)
                    )
                ) {
                    Text("Confirmar Resposta")
                }
            } else {
                // Result
                ResultCard(
                    scenario = scenario,
                    selectedAnswer = selectedAnswer ?: -1,
                    onNext = {
                        currentScenario++
                        selectedAnswer = null
                        showResult = false
                    }
                )
            }
        } else {
            // Final Score
            FinalScoreCard(
                score = score,
                total = scenarios.size,
                onRestart = {
                    currentScenario = 0
                    score = 0
                    selectedAnswer = null
                    showResult = false
                }
            )
        }
    }
}

@Composable
fun ScenarioCard(scenario: HandScenario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(Tokens.CardElevation)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Position and Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Posição: ${scenario.position}",
                    style = MaterialTheme.typography.titleMedium,
                    color = colorFromHex(Tokens.Primary)
                )
                Text(
                    text = scenario.cards,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White
                )
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Action and Villain
            Text(
                text = "Ação: ${scenario.action}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "Vilão: ${scenario.villain}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral)
            )
            
            Spacer(Modifier.height(8.dp))
            
            Text(
                text = "Pot: ${scenario.pot}",
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Positive)
            )
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = scenario.question,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
        }
    }
}

@Composable
fun OptionButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(Tokens.PillRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                colorFromHex(Tokens.Primary).copy(alpha = 0.3f) 
            else 
                colorFromHex(Tokens.Pill)
        ),
        border = if (isSelected) {
            androidx.compose.foundation.BorderStroke(2.dp, colorFromHex(Tokens.Primary))
        } else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.titleMedium,
            color = Color.White
        )
    }
}

@Composable
fun ResultCard(
    scenario: HandScenario,
    selectedAnswer: Int,
    onNext: () -> Unit
) {
    val isCorrect = selectedAnswer == scenario.correctAnswer
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(Tokens.CardElevation)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = if (isCorrect) "Correto" else "Incorreto",
                    tint = colorFromHex(if (isCorrect) Tokens.Positive else Tokens.Negative),
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(Modifier.width(12.dp))
                
                Text(
                    text = if (isCorrect) "Correto!" else "Incorreto",
                    style = MaterialTheme.typography.headlineMedium,
                    color = colorFromHex(if (isCorrect) Tokens.Positive else Tokens.Negative)
                )
            }
            
            Spacer(Modifier.height(16.dp))
            
            Text(
                text = "Resposta correta: ${scenario.options[scenario.correctAnswer]}",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )
            
            Spacer(Modifier.height(12.dp))
            
            Text(
                text = scenario.explanation,
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral)
            )
            
            Spacer(Modifier.height(20.dp))
            
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorFromHex(Tokens.Primary)
                )
            ) {
                Text("Próximo")
            }
        }
    }
}

@Composable
fun FinalScoreCard(
    score: Int,
    total: Int,
    onRestart: () -> Unit
) {
    val percentage = (score.toFloat() / total * 100).toInt()
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(Tokens.CardElevation)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Treinamento Concluído!",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White
            )
            
            Spacer(Modifier.height(20.dp))
            
            Text(
                text = "$score/$total",
                style = MaterialTheme.typography.displayLarge,
                color = colorFromHex(Tokens.Primary)
            )
            
            Text(
                text = "$percentage% de acerto",
                style = MaterialTheme.typography.titleMedium,
                color = colorFromHex(Tokens.Neutral)
            )
            
            Spacer(Modifier.height(20.dp))
            
            Button(
                onClick = onRestart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorFromHex(Tokens.Primary)
                )
            ) {
                Text("Treinar Novamente")
            }
        }
    }
}
