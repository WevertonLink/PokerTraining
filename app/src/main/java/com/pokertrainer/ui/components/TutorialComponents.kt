package com.pokertrainer.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.pokertrainer.ui.theme.Tokens
import com.pokertrainer.ui.theme.colorFromHex

@Composable
fun TutorialTooltip(
    visible: Boolean,
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onAction: (() -> Unit)? = null,
    actionLabel: String = "Entendido",
    icon: ImageVector = Icons.Default.Lightbulb,
    position: TooltipPosition = TooltipPosition.TOP,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(300)) + 
                scaleIn(initialScale = 0.8f, animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300)) + 
               scaleOut(targetScale = 0.8f, animationSpec = tween(300))
    ) {
        Popup(
            onDismissRequest = onDismiss,
            properties = PopupProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            TooltipContent(
                title = title,
                message = message,
                onDismiss = onDismiss,
                onAction = onAction,
                actionLabel = actionLabel,
                icon = icon,
                position = position,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun TooltipContent(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onAction: (() -> Unit)?,
    actionLabel: String,
    icon: ImageVector,
    position: TooltipPosition,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .shadow(8.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with icon and close button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = colorFromHex(Tokens.Primary),
                        modifier = Modifier.size(20.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = colorFromHex(Tokens.Neutral),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Message
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral),
                lineHeight = 20.sp
            )
            
            // Action button if provided
            if (onAction != null) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = {
                            onAction()
                            onDismiss()
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorFromHex(Tokens.Primary)
                        )
                    ) {
                        Text(
                            text = actionLabel,
                            color = Color.White,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        
        // Tooltip arrow (simplified)
        if (position != TooltipPosition.CENTER) {
            TooltipArrow(position)
        }
    }
}

@Composable
private fun TooltipArrow(position: TooltipPosition) {
    // Simplified arrow implementation
    // In a real implementation, you'd draw a proper arrow shape
    Box(
        modifier = Modifier
            .size(12.dp)
            .background(
                color = colorFromHex(Tokens.SurfaceElevated),
                shape = RoundedCornerShape(2.dp)
            )
    )
}

@Composable
fun ContextualHint(
    title: String,
    message: String,
    targetElementId: String,
    onDismiss: () -> Unit,
    onAction: (() -> Unit)? = null,
    priority: HintPriority = HintPriority.MEDIUM,
    autoShowDelay: Long = 1000L
) {
    var showHint by remember { mutableStateOf(false) }
    
    LaunchedEffect(targetElementId) {
        kotlinx.coroutines.delay(autoShowDelay)
        showHint = true
    }
    
    val hintColor = when (priority) {
        HintPriority.LOW -> colorFromHex(Tokens.Neutral)
        HintPriority.MEDIUM -> colorFromHex(Tokens.Primary)
        HintPriority.HIGH -> colorFromHex(Tokens.Warning)
        HintPriority.CRITICAL -> colorFromHex(Tokens.Error)
    }
    
    AnimatedVisibility(
        visible = showHint,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            )
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it }
        ) + fadeOut()
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = hintColor.copy(alpha = 0.1f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, 
                hintColor.copy(alpha = 0.3f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (priority) {
                        HintPriority.LOW -> Icons.Default.Info
                        HintPriority.MEDIUM -> Icons.Default.Lightbulb
                        HintPriority.HIGH -> Icons.Default.Warning
                        HintPriority.CRITICAL -> Icons.Default.Error
                    },
                    contentDescription = null,
                    tint = hintColor,
                    modifier = Modifier.size(20.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodySmall,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
                
                Row {
                    if (onAction != null) {
                        TextButton(
                            onClick = {
                                onAction()
                                onDismiss()
                                showHint = false
                            }
                        ) {
                            Text(
                                text = "Ação",
                                color = hintColor,
                                fontSize = 12.sp
                            )
                        }
                    }
                    
                    IconButton(
                        onClick = {
                            onDismiss()
                            showHint = false
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = colorFromHex(Tokens.Neutral),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun InteractiveTutorialOverlay(
    step: TutorialStep,
    isVisible: Boolean,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(animationSpec = tween(300)),
        exit = fadeOut(animationSpec = tween(300))
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.7f))
        ) {
            // Highlight target element (simplified implementation)
            step.targetElement?.let { targetId ->
                HighlightCircle(
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
            // Tutorial content
            TutorialStepCard(
                step = step,
                onNext = onNext,
                onPrevious = onPrevious,
                onSkip = onSkip,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(24.dp)
            )
        }
    }
}

@Composable
private fun HighlightCircle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(120.dp)
            .background(
                color = Color.White.copy(alpha = 0.1f),
                shape = androidx.compose.foundation.shape.CircleShape
            )
            .padding(4.dp)
            .background(
                color = Color.Transparent,
                shape = androidx.compose.foundation.shape.CircleShape
            )
    )
}

@Composable
private fun TutorialStepCard(
    step: TutorialStep,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = step.title,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = step.instruction,
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onPrevious) {
                    Text("Anterior", color = colorFromHex(Tokens.Neutral))
                }
                
                TextButton(onClick = onSkip) {
                    Text("Pular", color = colorFromHex(Tokens.Neutral))
                }
                
                Button(
                    onClick = onNext,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorFromHex(Tokens.Primary)
                    )
                ) {
                    Text("Próximo", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun FirstTimeUserGuide(
    isVisible: Boolean,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically(
            initialOffsetY = { it },
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy
            )
        ),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = colorFromHex(Tokens.Primary).copy(alpha = 0.1f)
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp, 
                colorFromHex(Tokens.Primary).copy(alpha = 0.3f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "🌟",
                        fontSize = 24.sp
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Bem-vindo ao PokerTrainer!",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Esta é sua primeira vez aqui. Que tal começar com um tutorial rápido para conhecer todas as funcionalidades?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colorFromHex(Tokens.Neutral)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(
                        onClick = onComplete
                    ) {
                        Text(
                            text = "Agora não",
                            color = colorFromHex(Tokens.Neutral)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    Button(
                        onClick = onComplete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorFromHex(Tokens.Primary)
                        )
                    ) {
                        Text(
                            text = "Começar Tutorial",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

enum class TooltipPosition {
    TOP, BOTTOM, LEFT, RIGHT, CENTER
}

enum class HintPriority {
    LOW, MEDIUM, HIGH, CRITICAL
}
