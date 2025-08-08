package com.pokertrainer.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.*
import com.pokertrainer.ui.viewmodels.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TutorialScreen(
    onBackPressed: () -> Unit,
    viewModel: TutorialViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Show onboarding if it's first launch
    if (uiState.showOnboarding) {
        OnboardingScreen(
            currentStep = uiState.onboardingStep,
            onNext = { viewModel.nextOnboardingStep() },
            onSkip = { viewModel.skipOnboarding() }
        )
        return
    }
    
    // Show active tutorial overlay
    uiState.activeTutorial?.let { tutorial ->
        TutorialOverlay(
            tutorial = tutorial,
            currentStep = uiState.currentStep,
            onNext = { viewModel.nextTutorialStep() },
            onPrevious = { viewModel.previousTutorialStep() },
            onSkip = { viewModel.skipTutorial() }
        )
        return
    }
    
    // Main tutorial catalog screen
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Tutoriais",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = backgroundGradient())
                .padding(padding)
                .padding(horizontal = Tokens.ScreenPad)
        ) {
            // Progress Overview
            TutorialProgressCard(
                completedCount = uiState.completedTutorials.size,
                totalCount = viewModel.getAvailableTutorials().size,
                progress = uiState.tutorialProgress
            )
            
            Spacer(modifier = Modifier.height(Tokens.SectionSpacing))
            
            // Available Hints
            if (uiState.availableHints.isNotEmpty()) {
                SectionHeader("💡 Dicas Personalizadas")
                HintsSection(
                    hints = uiState.availableHints,
                    onHintClick = { viewModel.markHintAsRead(it.id) },
                    onHintDismiss = { viewModel.dismissHint(it.id) }
                )
                
                Spacer(modifier = Modifier.height(Tokens.SectionSpacing))
            }
            
            // Tutorial Categories
            SectionHeader("📚 Tutoriais Disponíveis")
            TutorialCatalog(
                tutorials = viewModel.getAvailableTutorials(),
                completedTutorials = uiState.completedTutorials,
                onTutorialStart = { viewModel.startTutorial(it) }
            )
        }
    }
}

@Composable
private fun OnboardingScreen(
    currentStep: OnboardingStep,
    onNext: () -> Unit,
    onSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundGradient())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onSkip) {
                    Text(
                        text = "Pular",
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
            }
            
            // Content
            OnboardingContent(step = currentStep)
            
            // Navigation
            OnboardingNavigation(
                step = currentStep,
                onNext = onNext
            )
        }
    }
}

@Composable
private fun OnboardingContent(step: OnboardingStep) {
    val content = when (step) {
        OnboardingStep.WELCOME -> OnboardingWelcome()
        OnboardingStep.PROFILE_SETUP -> OnboardingProfileSetup()
        OnboardingStep.FEATURES_OVERVIEW -> OnboardingFeaturesOverview()
        OnboardingStep.FIRST_TRAINING -> OnboardingFirstTraining()
        OnboardingStep.GAMIFICATION_INTRO -> OnboardingGamificationIntro()
        OnboardingStep.NOTIFICATIONS_SETUP -> OnboardingNotificationsSetup()
        OnboardingStep.COMPLETE -> OnboardingComplete()
    }
    
    AnimatedContent(
        targetState = step,
        transitionSpec = {
            slideInHorizontally(
                initialOffsetX = { it },
                animationSpec = tween(300)
            ) togetherWith slideOutHorizontally(
                targetOffsetX = { -it },
                animationSpec = tween(300)
            )
        },
        label = "onboarding_content"
    ) { targetStep ->
        content
    }
}

@Composable
private fun OnboardingWelcome() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Text(
            text = "🃏",
            fontSize = 72.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "Bem-vindo ao PokerTrainer!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Sua jornada para se tornar um mestre do poker começa aqui. Vamos configurar tudo para você!",
            style = MaterialTheme.typography.bodyLarge,
            color = colorFromHex(Tokens.Neutral),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun OnboardingProfileSetup() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Perfil",
            tint = colorFromHex(Tokens.Primary),
            modifier = Modifier.size(72.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Configure seu Perfil",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Defina suas preferências e objetivos para que possamos personalizar sua experiência de aprendizado.",
            style = MaterialTheme.typography.bodyLarge,
            color = colorFromHex(Tokens.Neutral),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Quick setup options
        QuickSetupCard()
    }
}

@Composable
private fun OnboardingFeaturesOverview() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Text(
            text = "🚀",
            fontSize = 72.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "Recursos Principais",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        FeatureHighlightsList()
    }
}

@Composable
private fun OnboardingFirstTraining() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Icon(
            imageVector = Icons.Default.FitnessCenter,
            contentDescription = "Treino",
            tint = colorFromHex(Tokens.Primary),
            modifier = Modifier.size(72.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Primeiro Treino",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Vamos começar com um drill básico de pré-flop para avaliar seu nível atual e personalizar futuras recomendações.",
            style = MaterialTheme.typography.bodyLarge,
            color = colorFromHex(Tokens.Neutral),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun OnboardingGamificationIntro() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Text(
            text = "🎮",
            fontSize = 72.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "Sistema de Gamificação",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Ganhe XP, desbloqueie conquistas e suba no ranking global enquanto aprimora suas habilidades!",
            style = MaterialTheme.typography.bodyLarge,
            color = colorFromHex(Tokens.Neutral),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        GamificationPreview()
    }
}

@Composable
private fun OnboardingNotificationsSetup() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "Notificações",
            tint = colorFromHex(Tokens.Primary),
            modifier = Modifier.size(72.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Mantenha-se Motivado",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Configure notificações para lembretes de treino, conquistas desbloqueadas e muito mais!",
            style = MaterialTheme.typography.bodyLarge,
            color = colorFromHex(Tokens.Neutral),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun OnboardingComplete() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxHeight(0.7f)
    ) {
        Text(
            text = "🎉",
            fontSize = 72.sp,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "Tudo Pronto!",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "Sua jornada no PokerTrainer está apenas começando. Boa sorte e bons treinos!",
            style = MaterialTheme.typography.bodyLarge,
            color = colorFromHex(Tokens.Neutral),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun OnboardingNavigation(
    step: OnboardingStep,
    onNext: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress indicators
        OnboardingProgressIndicator(step)
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Next button
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorFromHex(Tokens.Primary)
            )
        ) {
            Text(
                text = if (step == OnboardingStep.COMPLETE) "Começar!" else "Próximo",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun OnboardingProgressIndicator(currentStep: OnboardingStep) {
    val steps = OnboardingStep.values().filter { it != OnboardingStep.COMPLETE }
    val currentIndex = steps.indexOf(currentStep)
    
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { index, _ ->
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(
                        color = if (index <= currentIndex) {
                            colorFromHex(Tokens.Primary)
                        } else {
                            colorFromHex(Tokens.Neutral).copy(alpha = 0.3f)
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}

@Composable
private fun TutorialProgressCard(
    completedCount: Int,
    totalCount: Int,
    progress: Map<String, Float>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Progresso nos Tutoriais",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$completedCount de $totalCount concluídos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                }
                
                CircularProgressIndicator(
                    progress = { completedCount.toFloat() / totalCount.toFloat() },
                    modifier = Modifier.size(48.dp),
                    color = colorFromHex(Tokens.Primary),
                    strokeWidth = 4.dp,
                )
            }
            
            if (progress.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Em andamento:",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorFromHex(Tokens.Neutral)
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                progress.forEach { (tutorialId, progressValue) ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tutorialId.replace("_", " ").replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        
                        LinearProgressIndicator(
                            progress = { progressValue },
                            modifier = Modifier
                                .width(80.dp)
                                .height(4.dp),
                            color = colorFromHex(Tokens.Primary),
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

@Composable
private fun HintsSection(
    hints: List<Hint>,
    onHintClick: (Hint) -> Unit,
    onHintDismiss: (Hint) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(hints.filter { !it.isRead }) { hint ->
            HintCard(
                hint = hint,
                onClick = { onHintClick(hint) },
                onDismiss = { onHintDismiss(hint) }
            )
        }
    }
}

@Composable
private fun HintCard(
    hint: Hint,
    onClick: () -> Unit,
    onDismiss: () -> Unit
) {
    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = when (hint.priority) {
                HintPriority.CRITICAL -> colorFromHex(Tokens.Error).copy(alpha = 0.1f)
                HintPriority.HIGH -> colorFromHex(Tokens.Warning).copy(alpha = 0.1f)
                HintPriority.MEDIUM -> colorFromHex(Tokens.Primary).copy(alpha = 0.1f)
                HintPriority.LOW -> colorFromHex(Tokens.SurfaceElevated)
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = hint.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Dispensar",
                        tint = colorFromHex(Tokens.Neutral),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            
            Text(
                text = hint.message,
                style = MaterialTheme.typography.bodySmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
private fun TutorialCatalog(
    tutorials: List<Tutorial>,
    completedTutorials: Set<String>,
    onTutorialStart: (String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Group by category
        val groupedTutorials = tutorials.groupBy { it.category }
        
        groupedTutorials.forEach { (category, categoryTutorials) ->
            item {
                Text(
                    text = getCategoryDisplayName(category),
                    style = MaterialTheme.typography.titleMedium,
                    color = colorFromHex(Tokens.Primary),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
            
            items(categoryTutorials) { tutorial ->
                TutorialCard(
                    tutorial = tutorial,
                    isCompleted = tutorial.id in completedTutorials,
                    onStart = { onTutorialStart(tutorial.id) }
                )
            }
        }
    }
}

@Composable
private fun TutorialCard(
    tutorial: Tutorial,
    isCompleted: Boolean,
    onStart: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) {
                colorFromHex(Tokens.Positive).copy(alpha = 0.1f)
            } else {
                colorFromHex(Tokens.SurfaceElevated)
            }
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tutorial.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        
                        if (isCompleted) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Concluído",
                                tint = colorFromHex(Tokens.Positive),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = tutorial.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DifficultyChip(tutorial.difficulty)
                        
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Duração",
                            tint = colorFromHex(Tokens.Neutral),
                            modifier = Modifier.size(16.dp)
                        )
                        
                        Spacer(modifier = Modifier.width(4.dp))
                        
                        Text(
                            text = "${tutorial.estimatedDuration}min",
                            style = MaterialTheme.typography.bodySmall,
                            color = colorFromHex(Tokens.Neutral)
                        )
                        
                        tutorial.rewards?.let { rewards ->
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            Text(
                                text = "+${rewards.xp} XP",
                                style = MaterialTheme.typography.bodySmall,
                                color = colorFromHex(Tokens.Warning),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                
                if (!isCompleted) {
                    Button(
                        onClick = onStart,
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorFromHex(Tokens.Primary)
                        )
                    ) {
                        Text(
                            text = "Iniciar",
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DifficultyChip(difficulty: TutorialDifficulty) {
    val (color, text) = when (difficulty) {
        TutorialDifficulty.BEGINNER -> colorFromHex(Tokens.Positive) to "Iniciante"
        TutorialDifficulty.INTERMEDIATE -> colorFromHex(Tokens.Warning) to "Intermediário"
        TutorialDifficulty.ADVANCED -> colorFromHex(Tokens.Error) to "Avançado"
        TutorialDifficulty.EXPERT -> colorFromHex("#9C27B0") to "Expert"
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun QuickSetupCard() {
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
                text = "Configuração Rápida",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                QuickSetupOption("🚀", "Iniciante")
                QuickSetupOption("💪", "Intermediário")
                QuickSetupOption("🏆", "Avançado")
            }
        }
    }
}

@Composable
private fun QuickSetupOption(emoji: String, level: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emoji, fontSize = 32.sp)
        Text(
            text = level,
            style = MaterialTheme.typography.labelSmall,
            color = colorFromHex(Tokens.Neutral)
        )
    }
}

@Composable
private fun FeatureHighlightsList() {
    val features = listOf(
        "🎯" to "Drills Personalizados",
        "📊" to "Análise de Performance",
        "🏆" to "Sistema de Gamificação",
        "🔔" to "Notificações Inteligentes",
        "📅" to "Agendamento com IA"
    )
    
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        features.forEach { (emoji, feature) ->
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = emoji, fontSize = 24.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = feature,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun GamificationPreview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Level 1",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "0 / 1000 XP",
                    style = MaterialTheme.typography.bodySmall,
                    color = colorFromHex(Tokens.Neutral)
                )
            }
            
            Text(
                text = "🏅",
                fontSize = 32.sp
            )
        }
    }
}

@Composable
private fun TutorialOverlay(
    tutorial: Tutorial,
    currentStep: Int,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onSkip: () -> Unit
) {
    // Implementation for tutorial overlay would go here
    // This would show step-by-step instructions with highlights
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
    ) {
        // Tutorial content overlay implementation
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(24.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = colorFromHex(Tokens.SurfaceElevated)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = tutorial.steps[currentStep].title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = tutorial.steps[currentStep].instruction,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        if (currentStep > 0) {
                            TextButton(onClick = onPrevious) {
                                Text("Anterior", color = colorFromHex(Tokens.Neutral))
                            }
                        } else {
                            Spacer(modifier = Modifier.width(1.dp))
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
                            Text(
                                text = if (currentStep == tutorial.steps.size - 1) "Concluir" else "Próximo",
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getCategoryDisplayName(category: TutorialCategory): String {
    return when (category) {
        TutorialCategory.ONBOARDING -> "🌟 Primeiros Passos"
        TutorialCategory.BASIC_FEATURES -> "📱 Recursos Básicos"
        TutorialCategory.ADVANCED_FEATURES -> "🚀 Recursos Avançados"
        TutorialCategory.GAMIFICATION -> "🎮 Gamificação"
        TutorialCategory.ANALYTICS -> "📊 Análise"
        TutorialCategory.PRACTICE -> "🎯 Prática"
        TutorialCategory.SOCIAL -> "👥 Social"
    }
}
