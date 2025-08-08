package com.pokertrainer.ui.screens.practice

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokertrainer.domain.models.DrillScenario
import com.pokertrainer.domain.models.TrainingDrill
import com.pokertrainer.domain.usecases.DecisionResult
import com.pokertrainer.ui.components.practice.RangeSelector
import com.pokertrainer.ui.components.practice.TimerDisplay
import com.pokertrainer.ui.viewmodels.PreflopDrillEvent
import com.pokertrainer.ui.viewmodels.PreflopDrillViewModel
import com.pokertrainer.ui.viewmodels.PreflopDrillViewModelFactory
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun PreflopDrillScreen(
    drillId: Int,
    onBackClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {}
) {
    val viewModel: PreflopDrillViewModel = viewModel(
        factory = PreflopDrillViewModelFactory(drillId)
    )
    
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    
    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is PreflopDrillEvent.DrillComplete -> {
                    Toast.makeText(context, "Drill completo! Acurácia: ${event.accuracy}%", Toast.LENGTH_LONG).show()
                    onCompleteClick()
                }
                PreflopDrillEvent.TimeExpired -> {
                    Toast.makeText(context, "Tempo esgotado!", Toast.LENGTH_SHORT).show()
                    onCompleteClick()
                }
            }
        }
    }
    
    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = colorFromHex(Tokens.Primary))
        }
        return
    }
    
    if (state.error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Erro: ${state.error}",
                color = colorFromHex(Tokens.Negative)
            )
        }
        return
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        // Header com timer e controles
        DrillHeader(
            drill = state.drill,
            timeRemaining = state.timeRemaining,
            currentScenario = state.currentQuestion + 1,
            totalScenarios = state.totalQuestions,
            onBackClick = onBackClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Progresso
        ProgressIndicator(
            current = state.currentQuestion + 1,
            total = state.totalQuestions,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Cenário atual
        ScenarioCard(state.currentScenario)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Range Selector
        RangeSelector(
            selectedCombos = state.selectedCombos,
            onComboSelected = viewModel::toggleCombo,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Feedback (se visível)
        if (state.showFeedback && state.lastResult != null) {
            FeedbackCard(
                result = state.lastResult?,
                correctRange = state.currentScenario.correctRanges,
                selectedRange = state.selectedCombos.toList()
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Botões de ação
        ActionButtons(
            showFeedback = state.showFeedback,
            hasSelection = state.selectedCombos.isNotEmpty(),
            isLastScenario = state.currentQuestion == state.totalQuestions - 1,
            onCheckClick = viewModel::submitAnswer,
            onNextClick = viewModel::nextQuestion,
            onResetClick = viewModel::resetSelection
        )
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun DrillHeader(
    drill: TrainingDrill,
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
                    text = drill.title,
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
            totalTime = drill.duration * 60 * 1000L
        )
    }
}

@Composable
private fun ProgressIndicator(current: Int, total: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = { current.toFloat() / total.toFloat() },
            modifier = Modifier.size(60.dp),
            color = colorFromHex(Tokens.Primary),
            strokeWidth = 6.dp
        )
        Text(
            text = "$current/$total",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White
        )
    }
}

@Composable
private fun ScenarioCard(scenario: DrillScenario) {
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Posição: ${scenario.position.name}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorFromHex(Tokens.Primary)
                )
                
                Text(
                    text = scenario.action.name.replace("_", " "),
                    fontSize = 12.sp,
                    color = colorFromHex(Tokens.Neutral),
                    modifier = Modifier
                        .background(
                            color = colorFromHex(Tokens.Pill),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = scenario.description,
                fontSize = 12.sp,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Qual range você deve usar?",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }
    }
}

@Composable
private fun FeedbackCard(
    result: DecisionResult,
    correctRange: List<String>,
    selectedRange: List<String>
) {
    val backgroundColor = if (result.isCorrect) {
        colorFromHex(Tokens.Positive).copy(alpha = 0.2f)
    } else {
        colorFromHex(Tokens.Negative).copy(alpha = 0.2f)
    }
    
    val borderColor = if (result.isCorrect) {
        colorFromHex(Tokens.Positive)
    } else {
        colorFromHex(Tokens.Negative)
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(Tokens.CardRadius)
            ),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (result.isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = if (result.isCorrect) "Correto" else "Incorreto",
                    tint = if (result.isCorrect) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Negative)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Column {
                    Text(
                        text = if (result.isCorrect) "Correto!" else "Incorreto",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (result.isCorrect) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Negative)
                    )
                    
                    Text(
                        text = "Precisão: ${result.accuracy.toInt()}%",
                        fontSize = 12.sp,
                        color = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = result.feedback,
                fontSize = 12.sp,
                color = Color.White
            )
            
            if (!result.isCorrect) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Range correto:",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = correctRange.joinToString(", "),
                    fontSize = 11.sp,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
        }
    }
}

@Composable
private fun ActionButtons(
    showFeedback: Boolean,
    hasSelection: Boolean,
    isLastScenario: Boolean,
    onCheckClick: () -> Unit,
    onNextClick: () -> Unit,
    onResetClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (!showFeedback) {
            // Botão de limpar
            OutlinedButton(
                onClick = onResetClick,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(Tokens.PillRadius)
            ) {
                Text(
                    text = "Limpar",
                    color = colorFromHex(Tokens.Neutral)
                )
            }
            
            // Botão de verificar
            Button(
                onClick = onCheckClick,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorFromHex(Tokens.Primary)
                ),
                shape = RoundedCornerShape(Tokens.PillRadius),
                enabled = hasSelection
            ) {
                Text(
                    text = "Confirmar",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        } else {
            // Botão de próximo/finalizar
            Button(
                onClick = onNextClick,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorFromHex(Tokens.Primary)
                ),
                shape = RoundedCornerShape(Tokens.PillRadius)
            ) {
                Text(
                    text = if (isLastScenario) "Finalizar" else "Próximo",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
