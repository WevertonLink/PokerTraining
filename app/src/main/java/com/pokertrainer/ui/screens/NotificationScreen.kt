package com.pokertrainer.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pokertrainer.ui.components.*
import com.pokertrainer.ui.theme.*
import com.pokertrainer.ui.viewmodels.*
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    onBackPressed: () -> Unit,
    viewModel: NotificationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    
    // Toast notification overlay
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Notificações",
                                style = MaterialTheme.typography.headlineSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                            
                            if (uiState.unreadCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                
                                Badge(
                                    containerColor = colorFromHex(Tokens.Error)
                                ) {
                                    Text(
                                        text = uiState.unreadCount.toString(),
                                        color = Color.White,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
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
                    actions = {
                        // Mark all as read
                        if (uiState.unreadCount > 0) {
                            IconButton(
                                onClick = { viewModel.markAllAsRead() }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DoneAll,
                                    contentDescription = "Marcar todas como lidas",
                                    tint = Color.White
                                )
                            }
                        }
                        
                        // Settings
                        IconButton(
                            onClick = { /* TODO: Open settings */ }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurações",
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
                // Quick Actions Row
                QuickActionsRow(viewModel)
                
                Spacer(modifier = Modifier.height(Tokens.SectionSpacing))
                
                // Notifications List
                if (uiState.notifications.isEmpty()) {
                    EmptyNotificationsCard()
                } else {
                    NotificationsList(
                        notifications = uiState.notifications,
                        onMarkAsRead = { viewModel.markAsRead(it) },
                        onDelete = { viewModel.deleteNotification(it) },
                        onAction = { notificationId, action ->
                            // Handle notification actions
                            handleNotificationAction(notificationId, action, viewModel)
                        }
                    )
                }
            }
        }
        
        // Toast notification overlay
        uiState.activeToast?.let { toast ->
            ToastNotificationOverlay(
                toast = toast,
                onDismiss = { viewModel.dismissToast() },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
private fun QuickActionsRow(viewModel: NotificationViewModel) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        QuickActionCard(
            title = "Conquistas",
            subtitle = "Ver todas",
            icon = Icons.Default.EmojiEvents,
            color = colorFromHex(Tokens.Warning),
            modifier = Modifier.weight(1f),
            onClick = {
                viewModel.triggerAchievementNotification("Teste de Conquista", 250)
            }
        )
        
        QuickActionCard(
            title = "Desafios",
            subtitle = "3 ativos",
            icon = Icons.Default.SportsMma,
            color = colorFromHex(Tokens.Primary),
            modifier = Modifier.weight(1f),
            onClick = {
                viewModel.triggerDailyChallengeNotification("Desafio Teste", 100, "2 horas")
            }
        )
        
        QuickActionCard(
            title = "Sequência",
            subtitle = "7 dias",
            icon = Icons.Default.LocalFireDepartment,
            color = colorFromHex(Tokens.Error),
            modifier = Modifier.weight(1f),
            onClick = {
                viewModel.triggerStreakWarning(7, 3)
            }
        )
    }
}

@Composable
private fun QuickActionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
private fun NotificationsList(
    notifications: List<AppNotification>,
    onMarkAsRead: (String) -> Unit,
    onDelete: (String) -> Unit,
    onAction: (String, String) -> Unit
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Group notifications by priority
        val groupedNotifications = notifications.groupBy { it.priority }
        
        groupedNotifications[NotificationPriority.URGENT]?.let { urgentNotifications ->
            if (urgentNotifications.isNotEmpty()) {
                item {
                    NotificationGroupHeader("🚨 Urgente")
                }
                items(urgentNotifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkAsRead = onMarkAsRead,
                        onDelete = onDelete,
                        onAction = onAction
                    )
                }
            }
        }
        
        groupedNotifications[NotificationPriority.HIGH]?.let { highNotifications ->
            if (highNotifications.isNotEmpty()) {
                item {
                    NotificationGroupHeader("⚡ Alta Prioridade")
                }
                items(highNotifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkAsRead = onMarkAsRead,
                        onDelete = onDelete,
                        onAction = onAction
                    )
                }
            }
        }
        
        groupedNotifications[NotificationPriority.NORMAL]?.let { normalNotifications ->
            if (normalNotifications.isNotEmpty()) {
                item {
                    NotificationGroupHeader("📋 Recentes")
                }
                items(normalNotifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkAsRead = onMarkAsRead,
                        onDelete = onDelete,
                        onAction = onAction
                    )
                }
            }
        }
        
        groupedNotifications[NotificationPriority.LOW]?.let { lowNotifications ->
            if (lowNotifications.isNotEmpty()) {
                item {
                    NotificationGroupHeader("💬 Outras")
                }
                items(lowNotifications) { notification ->
                    NotificationCard(
                        notification = notification,
                        onMarkAsRead = onMarkAsRead,
                        onDelete = onDelete,
                        onAction = onAction
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationGroupHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        color = colorFromHex(Tokens.Primary),
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onMarkAsRead: (String) -> Unit,
    onDelete: (String) -> Unit,
    onAction: (String, String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                colorFromHex(Tokens.SurfaceElevated).copy(alpha = 0.7f)
            } else {
                colorFromHex(Tokens.SurfaceElevated)
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (notification.isRead) 2.dp else 4.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Notification icon
                Text(
                    text = notification.icon,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.size(32.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                // Content
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = notification.title,
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        
                        if (!notification.isRead) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = colorFromHex(Tokens.Primary),
                                        shape = androidx.compose.foundation.shape.CircleShape
                                    )
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = notification.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorFromHex(Tokens.Neutral),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = formatTimestamp(notification.timestamp),
                            style = MaterialTheme.typography.labelSmall,
                            color = colorFromHex(Tokens.Neutral)
                        )
                        
                        // Priority indicator
                        if (notification.priority == NotificationPriority.HIGH || 
                            notification.priority == NotificationPriority.URGENT) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.PriorityHigh,
                                contentDescription = "Alta prioridade",
                                tint = when (notification.priority) {
                                    NotificationPriority.URGENT -> colorFromHex(Tokens.Error)
                                    NotificationPriority.HIGH -> colorFromHex(Tokens.Warning)
                                    else -> colorFromHex(Tokens.Neutral)
                                },
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        
                        // Expiration indicator
                        notification.expiresAt?.let { expiresAt ->
                            val hoursLeft = ChronoUnit.HOURS.between(
                                notification.timestamp, 
                                expiresAt
                            ).toInt()
                            
                            if (hoursLeft <= 24) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Schedule,
                                        contentDescription = "Expira",
                                        tint = colorFromHex(Tokens.Warning),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(2.dp))
                                    Text(
                                        text = "${hoursLeft}h",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = colorFromHex(Tokens.Warning)
                                    )
                                }
                            }
                        }
                    }
                }
                
                // Actions menu
                NotificationActionsMenu(
                    notification = notification,
                    onMarkAsRead = onMarkAsRead,
                    onDelete = onDelete
                )
            }
            
            // Action button
            notification.actionLabel?.let { actionLabel ->
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Button(
                        onClick = { 
                            onAction(notification.id, notification.actionData ?: "")
                            onMarkAsRead(notification.id)
                        },
                        shape = RoundedCornerShape(20.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorFromHex(Tokens.Primary)
                        )
                    ) {
                        Text(
                            text = actionLabel,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationActionsMenu(
    notification: AppNotification,
    onMarkAsRead: (String) -> Unit,
    onDelete: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Mais opções",
                tint = colorFromHex(Tokens.Neutral)
            )
        }
        
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (!notification.isRead) {
                DropdownMenuItem(
                    text = { Text("Marcar como lida") },
                    onClick = {
                        onMarkAsRead(notification.id)
                        expanded = false
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Done,
                            contentDescription = null
                        )
                    }
                )
            }
            
            DropdownMenuItem(
                text = { Text("Excluir") },
                onClick = {
                    onDelete(notification.id)
                    expanded = false
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun EmptyNotificationsCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Tokens.CardRadius),
        colors = CardDefaults.cardColors(
            containerColor = colorFromHex(Tokens.SurfaceElevated)
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Sem notificações",
                tint = colorFromHex(Tokens.Neutral),
                modifier = Modifier.size(64.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Tudo em dia!",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = "Você não tem notificações pendentes",
                style = MaterialTheme.typography.bodyMedium,
                color = colorFromHex(Tokens.Neutral)
            )
        }
    }
}

@Composable
private fun ToastNotificationOverlay(
    toast: ToastNotification,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeIn(),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(300)
        ) + fadeOut(),
        modifier = modifier.padding(16.dp)
    ) {
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = when (toast.type) {
                    ToastType.SUCCESS -> colorFromHex(Tokens.Positive)
                    ToastType.WARNING -> colorFromHex(Tokens.Warning)
                    ToastType.ERROR -> colorFromHex(Tokens.Error)
                    ToastType.INFO -> colorFromHex(Tokens.Primary)
                }
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when (toast.type) {
                        ToastType.SUCCESS -> Icons.Default.CheckCircle
                        ToastType.WARNING -> Icons.Default.Warning
                        ToastType.ERROR -> Icons.Default.Error
                        ToastType.INFO -> Icons.Default.Info
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = toast.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: java.time.LocalDateTime): String {
    val now = java.time.LocalDateTime.now()
    val diff = ChronoUnit.MINUTES.between(timestamp, now)
    
    return when {
        diff < 1 -> "Agora"
        diff < 60 -> "${diff}min"
        diff < 1440 -> "${diff / 60}h"
        else -> timestamp.format(DateTimeFormatter.ofPattern("dd/MM"))
    }
}

private fun handleNotificationAction(
    notificationId: String, 
    action: String, 
    viewModel: NotificationViewModel
) {
    // Handle different notification actions
    when (action) {
        "Ver Conquista" -> {
            // Navigate to achievements screen
            viewModel.showToast("Navegando para conquistas...", ToastType.INFO)
        }
        "Ver Recompensas" -> {
            // Navigate to rewards screen
            viewModel.showToast("Navegando para recompensas...", ToastType.INFO)
        }
        "Treinar Agora" -> {
            // Navigate to training screen
            viewModel.showToast("Iniciando sessão de treino...", ToastType.SUCCESS)
        }
        "Completar" -> {
            // Navigate to challenge
            viewModel.showToast("Abrindo desafio...", ToastType.INFO)
        }
        else -> {
            viewModel.showToast("Ação executada!", ToastType.SUCCESS)
        }
    }
}
