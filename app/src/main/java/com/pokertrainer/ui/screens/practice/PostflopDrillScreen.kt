package com.pokertrainer.ui.screens.practice

import androidx.compose.foundation.background
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
import com.pokertrainer.ui.components.practice.HandHistoryViewer
import com.pokertrainer.ui.components.practice.HandHistory
import com.pokertrainer.ui.components.practice.HandAction
import com.pokertrainer.ui.components.practice.TimerDisplay
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.backgroundGradient
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun PostflopDrillScreen(
    sessionId: String,
    onBackClick: () -> Unit = {},
    onCompleteClick: () -> Unit = {}
) {
    var currentHandIndex by remember { mutableStateOf(0) }
    var timeRemaining by remember { mutableStateOf(25 * 60 * 1000L) } // 25 minutos
    var userDecision by remember { mutableStateOf<String?>(null) }
    var showFeedback by remember { mutableStateOf(false) }
    
    val sampleHands = remember {
        listOf(
            HandHistory(
                handId = "H001",
                blinds = "$1/$2 NL",
                heroPosition = "BTN",
                heroCards = "AhKd",
                preflopActions = listOf(
                    HandAction("UTG", "fold", position = "UTG"),
                    HandAction("MP", "fold", position = "MP"),
                    HandAction("CO", "raise", 6.0, "CO"),
                    HandAction("BTN", "call", 6.0, "BTN")
                ),
                flopCards = "As 7h 2c",
                flopActions = listOf(
                    HandAction("CO", "bet", 8.0, "CO")
                ),
                result = "Você deve: CALL"
            ),
            HandHistory(
                handId = "H002",
                blinds = "$1/$2 NL",
                heroPosition = "BB",
                heroCards = "QsQh",
                preflopActions = listOf(
                    HandAction("UTG", "raise", 6.0, "UTG"),
                    HandAction("BB", "call", 6.0, "BB")
                ),
                flopCards = "Kh 9s 4d",
                flopActions = listOf(
                    HandAction("BB", "check", position = "BB"),
                    HandAction("UTG", "bet", 9.0, "UTG")
                ),
                result = "Você deve: FOLD"
            )
        )
    }
    
    val currentHand = sampleHands[currentHandIndex]
    val isLastHand = currentHandIndex == sampleHands.size - 1
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient())
            .padding(horizontal = Tokens.ScreenPad)
    ) {
        // Header
        PostflopHeader(
            sessionId = sessionId,
            timeRemaining = timeRemaining,
            currentHand = currentHandIndex + 1,
            totalHands = sampleHands.size,
            onBackClick = onBackClick
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Hand History Viewer
        HandHistoryViewer(
            handHistory = currentHand,
            modifier = Modifier.weight(1f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Decision Area
        if (!showFeedback) {
            DecisionButtonsArea(
                onDecision = { decision ->
                    userDecision = decision
                    showFeedback = true
                }
            )
        } else {
            FeedbackArea(
                userDecision = userDecision ?: "",
                correctDecision = currentHand.result,
                onNext = {
                    if (isLastHand) {
                        onCompleteClick()
                    } else {
                        currentHandIndex++
                        userDecision = null
                        showFeedback = false
                    }
                },
                isLastHand = isLastHand
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun PostflopHeader(
    sessionId: String,
    timeRemaining: Long,
    currentHand: Int,
    totalHands: Int,
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
                    text = "Simulação Pós-flop",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Mão $currentHand de $totalHands",
                    fontSize = 12.sp,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
        }
        
        TimerDisplay(
            timeRemaining = timeRemaining,
            totalTime = 25 * 60 * 1000L
        )
    }
}

@Composable
private fun DecisionButtonsArea(
    onDecision: (String) -> Unit
) {
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
                text = "Qual sua decisão?",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onDecision("FOLD") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorFromHex(Tokens.Negative)
                    ),
                    shape = RoundedCornerShape(Tokens.PillRadius)
                ) {
                    Text("FOLD", color = Color.White)
                }
                
                Button(
                    onClick = { onDecision("CALL") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorFromHex(Tokens.Neutral)
                    ),
                    shape = RoundedCornerShape(Tokens.PillRadius)
                ) {
                    Text("CALL", color = Color.White)
                }
                
                Button(
                    onClick = { onDecision("RAISE") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorFromHex(Tokens.Positive)
                    ),
                    shape = RoundedCornerShape(Tokens.PillRadius)
                ) {
                    Text("RAISE", color = Color.White)
                }
            }
        }
    }
}

@Composable
private fun FeedbackArea(
    userDecision: String,
    correctDecision: String,
    onNext: () -> Unit,
    isLastHand: Boolean
) {
    val isCorrect = userDecision in correctDecision
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isCorrect) {
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
                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                    contentDescription = if (isCorrect) "Correto" else "Incorreto",
                    tint = if (isCorrect) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Negative)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = if (isCorrect) "Correto!" else "Incorreto",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isCorrect) colorFromHex(Tokens.Positive) else colorFromHex(Tokens.Negative)
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Sua decisão: $userDecision",
                fontSize = 14.sp,
                color = Color.White
            )
            
            Text(
                text = correctDecision,
                fontSize = 14.sp,
                color = colorFromHex(Tokens.Primary),
                fontWeight = FontWeight.Medium
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
                    text = if (isLastHand) "Finalizar" else "Próxima Mão",
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
