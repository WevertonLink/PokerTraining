package com.pokertrainer.ui.screens.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pokertrainer.ui.components.practice.RangeSelector
import com.pokertrainer.ui.components.practice.TimerDisplay
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun HandReadingScreen(
    onBackClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {}
) {
    var currentScenarioIndex by remember { mutableStateOf(0) }
    var selectedRange by remember { mutableStateOf(setOf<String>()) }
    var timeRemaining by remember { mutableStateOf(20 * 60 * 1000L) } // 20 minutos
    var showFeedback by remember { mutableStateOf(false) }
    
    val scenarios = remember {
        listOf(
            HandReadingScenario(
                situation = "UTG abre para 3x. Você está no BB com KK e decide 3-bet.",
                villainAction = "4-bet para 24bb",
                question = "Qual o range provável do oponente?",
                correctRange = listOf("AA", "KK", "AKs", "AKo"),
                explanation = "Em posição UTG vs BB, um 4-bet geralmente indica mãos premium. KK está no borderline aqui."
            ),
            HandReadingScenario(
                situation = "6-max, BTN vs BB. BTN abre 2.5x, você defende com A7s.",
                villainAction = "Flop: A♠7♥2♣ - BTN aposta 60% pot",
                question = "Range de c-bet do BTN neste board?",
                correctRange = listOf("AA", "77", "22", "AK", "AQ", "AJ", "AT", "A9", "A8", "A7", "A6", "A5", "A4", "A3", "A2", "KK", "QQ", "JJ", "TT", "99", "88"),
                explanation = "Board muito seco favorece quem abriu. BTN pode apostar wide para value e proteção."
            ),
            HandReadingScenario(
                situation = "CO abre 2.5x, BTN call, SB fold, você defende BB com 8♦6♦.",
                villainAction = "Flop: K♠9♥4♣ - CO aposta, BTN fold, você call. Turn: 7♦ - CO aposta grande",
                question = "Range do CO no turn após second barrel?",
                correctRange = listOf("AA", "KK", "99", "44", "AK", "KQ", "KJ", "K9", "AT", "AJ", "QJ", "JT", "T8"),
                explanation = "Segunda aposta indica mãos que melhoraram ou draws fortes. CO polariza entre value e bluffs."
            )
        )
    }
    
    val currentScenario = scenarios[currentScenarioIndex]
    val isLastScenario = currentScenarioIndex == scenarios.size - 1
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        // Header
        HandReadingHeader(
            timeRemaining = timeRemaining,
            currentScenario = currentScenarioIndex + 1,
            totalScenarios = scenarios.size,
            onBackClick = onBackClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cenário atual
        ScenarioDetailCard(currentScenario)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Range Selector
        RangeSelector(
            selectedCombos = selectedRange,
            onComboSelected = { combo ->
                selectedRange = if (combo in selectedRange) {
                    selectedRange - combo
                } else {
                    selectedRange + combo
                }
            },
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Feedback
        if (showFeedback) {
            HandReadingFeedback(
                userRange = selectedRange.toList(),
                correctRange = currentScenario.correctRange,
                explanation = currentScenario.explanation,
                onNext = {
                    if (isLastScenario) {
                        onCompleteClick()
                    } else {
                        currentScenarioIndex++
                        selectedRange = setOf()
                        showFeedback = false
                    }
                },
                isLastScenario = isLastScenario
            )
        } else {
            // Botões de ação
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = { selectedRange = setOf() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(Tokens.PillRadius)
                ) {
                    Text("Limpar", color = colorFromHex(Tokens.Neutral))
                }
                
                Button(
                    onClick = { showFeedback = true },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorFromHex(Tokens.Primary)
                    ),
                    shape = RoundedCornerShape(Tokens.PillRadius),
                    enabled = selectedRange.isNotEmpty()
                ) {
                    Text("Analisar", color = Color.White)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

data class HandReadingScenario(
    val situation: String,
    val villainAction: String,
    val question: String,
    val correctRange: List<String>,
    val explanation: String
)

@Composable
private fun HandReadingHeader(
    timeRemaining: Long,
    currentScenario: Int,
    totalScenarios: Int,
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Voltar",
                    tint = Color.White
                )
            }
            
            Column {
                Text(
                    text = "Leitura de Mãos",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Cenário $currentScenario de $totalScenarios",
                    fontSize = 12.sp,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
        }
        
        TimerDisplay(
            timeRemaining = timeRemaining,
            totalTime = 20 * 60 * 1000L
        )
    }
}

@Composable
private fun ScenarioDetailCard(scenario: HandReadingScenario) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Situação:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorFromHex(Tokens.Primary)
            )
            Text(
                text = scenario.situation,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "Ação do Oponente:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = colorFromHex(Tokens.Primary)
            )
            Text(
                text = scenario.villainAction,
                fontSize = 14.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = scenario.question,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = colorFromHex("#FFB800")
            )
        }
    }
}

@Composable
private fun HandReadingFeedback(
    userRange: List<String>,
    correctRange: List<String>,
    explanation: String,
    onNext: () -> Unit,
    isLastScenario: Boolean
) {
    val accuracy = calculateRangeAccuracy(userRange, correctRange)
    val isGoodRead = accuracy >= 70.0
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isGoodRead) {
                colorFromHex(Tokens.Positive).copy(alpha = 0.2f)
            } else {
                colorFromHex(Tokens.Negative).copy(alpha = 0.2f)
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isGoodRead) Icons.Default.CheckCircle else Icons.Default.Psychology,
                    contentDescription = "Resultado",
                    tint = if (isGoodRead) colorFromHex(Tokens.Positive) else colorFromHex("#FFB800")
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = if (isGoodRead) "Boa leitura!" else "Pode melhorar",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isGoodRead) colorFromHex(Tokens.Positive) else colorFromHex("#FFB800")
                    )
                    Text(
                        text = "Precisão: ${accuracy.toInt()}%",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Análise:",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = explanation,
                fontSize = 12.sp,
                color = colorFromHex(Tokens.Neutral),
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Text(
                text = "Range esperado:",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = correctRange.joinToString(", "),
                fontSize = 11.sp,
                color = colorFromHex(Tokens.Primary)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Button(
                onClick = onNext,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorFromHex(Tokens.Primary)
                ),
                shape = RoundedCornerShape(Tokens.PillRadius)
            ) {
                Text(
                    text = if (isLastScenario) "Finalizar" else "Próximo Cenário",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

private fun calculateRangeAccuracy(userRange: List<String>, correctRange: List<String>): Double {
    val userSet = userRange.toSet()
    val correctSet = correctRange.toSet()
    
    val intersection = userSet.intersect(correctSet).size
    val union = userSet.union(correctSet).size
    
    return if (union == 0) 100.0 else (intersection.toDouble() / union.toDouble()) * 100.0
}
