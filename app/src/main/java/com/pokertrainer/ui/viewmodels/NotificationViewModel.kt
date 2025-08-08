package com.pokertrainer.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class NotificationUiState(
    val notifications: List<AppNotification> = emptyList(),
    val activeToast: ToastNotification? = null,
    val unreadCount: Int = 0,
    val settings: NotificationSettings = NotificationSettings(),
    val isLoading: Boolean = false
)

data class AppNotification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val timestamp: LocalDateTime,
    val isRead: Boolean = false,
    val actionLabel: String? = null,
    val actionData: String? = null,
    val priority: NotificationPriority = NotificationPriority.NORMAL,
    val icon: String = "🔔",
    val expiresAt: LocalDateTime? = null
)

data class ToastNotification(
    val id: String,
    val message: String,
    val type: ToastType,
    val duration: Long = 3000L,
    val action: String? = null
)

data class NotificationSettings(
    val achievementsEnabled: Boolean = true,
    val trainingSessions: Boolean = true,
    val streakReminders: Boolean = true,
    val dailyChallenges: Boolean = true,
    val socialUpdates: Boolean = true,
    val quietHours: QuietHours = QuietHours()
)

data class QuietHours(
    val enabled: Boolean = false,
    val startTime: String = "22:00",
    val endTime: String = "08:00"
)

enum class NotificationType {
    ACHIEVEMENT_UNLOCKED,
    LEVEL_UP,
    STREAK_WARNING,
    STREAK_MILESTONE,
    DAILY_CHALLENGE,
    TRAINING_REMINDER,
    SOCIAL_UPDATE,
    SYSTEM_UPDATE,
    REWARD_AVAILABLE
}

enum class NotificationPriority {
    LOW, NORMAL, HIGH, URGENT
}

enum class ToastType {
    SUCCESS, WARNING, ERROR, INFO
}

class NotificationViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(NotificationUiState())
    val uiState: StateFlow<NotificationUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
        startPeriodicChecks()
    }

    private fun loadNotifications() {
        viewModelScope.launch {
            val notifications = generateSampleNotifications()
            _uiState.value = _uiState.value.copy(
                notifications = notifications,
                unreadCount = notifications.count { !it.isRead }
            )
        }
    }

    fun markAsRead(notificationId: String) {
        viewModelScope.launch {
            val updatedNotifications = _uiState.value.notifications.map { notification ->
                if (notification.id == notificationId && !notification.isRead) {
                    notification.copy(isRead = true)
                } else notification
            }
            
            _uiState.value = _uiState.value.copy(
                notifications = updatedNotifications,
                unreadCount = updatedNotifications.count { !it.isRead }
            )
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val updatedNotifications = _uiState.value.notifications.map { 
                it.copy(isRead = true) 
            }
            
            _uiState.value = _uiState.value.copy(
                notifications = updatedNotifications,
                unreadCount = 0
            )
        }
    }

    fun deleteNotification(notificationId: String) {
        viewModelScope.launch {
            val updatedNotifications = _uiState.value.notifications.filter { 
                it.id != notificationId 
            }
            
            _uiState.value = _uiState.value.copy(
                notifications = updatedNotifications,
                unreadCount = updatedNotifications.count { !it.isRead }
            )
        }
    }

    fun showToast(message: String, type: ToastType, duration: Long = 3000L) {
        viewModelScope.launch {
            val toast = ToastNotification(
                id = "toast_${System.currentTimeMillis()}",
                message = message,
                type = type,
                duration = duration
            )
            
            _uiState.value = _uiState.value.copy(activeToast = toast)
            
            delay(duration)
            
            _uiState.value = _uiState.value.copy(activeToast = null)
        }
    }

    fun dismissToast() {
        _uiState.value = _uiState.value.copy(activeToast = null)
    }

    fun updateSettings(settings: NotificationSettings) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(settings = settings)
            // TODO: Persist settings to storage
        }
    }

    fun triggerAchievementNotification(achievementTitle: String, xpReward: Int) {
        viewModelScope.launch {
            val notification = AppNotification(
                id = "achievement_${System.currentTimeMillis()}",
                type = NotificationType.ACHIEVEMENT_UNLOCKED,
                title = "🏆 Conquista Desbloqueada!",
                message = "$achievementTitle (+$xpReward XP)",
                timestamp = LocalDateTime.now(),
                priority = NotificationPriority.HIGH,
                icon = "🏆",
                actionLabel = "Ver Conquista"
            )
            
            addNotification(notification)
            showToast("Conquista desbloqueada: $achievementTitle", ToastType.SUCCESS)
        }
    }

    fun triggerLevelUpNotification(newLevel: Int, rewards: String) {
        viewModelScope.launch {
            val notification = AppNotification(
                id = "levelup_${System.currentTimeMillis()}",
                type = NotificationType.LEVEL_UP,
                title = "🎉 Level Up!",
                message = "Parabéns! Você alcançou o nível $newLevel! $rewards",
                timestamp = LocalDateTime.now(),
                priority = NotificationPriority.HIGH,
                icon = "🎉",
                actionLabel = "Ver Recompensas"
            )
            
            addNotification(notification)
            showToast("Level Up! Nível $newLevel alcançado!", ToastType.SUCCESS)
        }
    }

    fun triggerStreakWarning(currentStreak: Int, hoursLeft: Int) {
        viewModelScope.launch {
            val notification = AppNotification(
                id = "streak_warning_${System.currentTimeMillis()}",
                type = NotificationType.STREAK_WARNING,
                title = "⚠️ Streak em Risco!",
                message = "Sua sequência de $currentStreak dias expira em ${hoursLeft}h. Faça uma sessão rápida!",
                timestamp = LocalDateTime.now(),
                priority = NotificationPriority.HIGH,
                icon = "⚠️",
                actionLabel = "Treinar Agora",
                expiresAt = LocalDateTime.now().plusHours(hoursLeft.toLong())
            )
            
            addNotification(notification)
            showToast("Atenção: Sua sequência expira em ${hoursLeft}h!", ToastType.WARNING)
        }
    }

    fun triggerDailyChallengeNotification(challengeTitle: String, xpReward: Int, timeLeft: String) {
        viewModelScope.launch {
            val notification = AppNotification(
                id = "challenge_${System.currentTimeMillis()}",
                type = NotificationType.DAILY_CHALLENGE,
                title = "🎯 Desafio Diário",
                message = "$challengeTitle expira em $timeLeft (+$xpReward XP)",
                timestamp = LocalDateTime.now(),
                priority = NotificationPriority.NORMAL,
                icon = "🎯",
                actionLabel = "Completar"
            )
            
            addNotification(notification)
        }
    }

    private fun addNotification(notification: AppNotification) {
        val updatedNotifications = listOf(notification) + _uiState.value.notifications
        _uiState.value = _uiState.value.copy(
            notifications = updatedNotifications,
            unreadCount = _uiState.value.unreadCount + 1
        )
    }

    private fun startPeriodicChecks() {
        viewModelScope.launch {
            while (true) {
                delay(300000) // Check every 5 minutes
                
                // Check for streak risks
                checkStreakRisk()
                
                // Check for daily challenge deadlines
                checkDailyChallengeDeadlines()
                
                // Remove expired notifications
                removeExpiredNotifications()
            }
        }
    }

    private suspend fun checkStreakRisk() {
        // Simulate streak risk check
        val currentHour = LocalDateTime.now().hour
        if (currentHour == 21) { // 9 PM warning
            triggerStreakWarning(7, 3)
        }
    }

    private suspend fun checkDailyChallengeDeadlines() {
        // Simulate challenge deadline check
        val currentHour = LocalDateTime.now().hour
        if (currentHour == 20) { // 8 PM reminder
            triggerDailyChallengeNotification("Precisão Diária", 150, "4 horas")
        }
    }

    private suspend fun removeExpiredNotifications() {
        val now = LocalDateTime.now()
        val activeNotifications = _uiState.value.notifications.filter { notification ->
            notification.expiresAt?.isAfter(now) ?: true
        }
        
        if (activeNotifications.size != _uiState.value.notifications.size) {
            _uiState.value = _uiState.value.copy(
                notifications = activeNotifications,
                unreadCount = activeNotifications.count { !it.isRead }
            )
        }
    }

    private fun generateSampleNotifications(): List<AppNotification> {
        return listOf(
            AppNotification(
                id = "notif_1",
                type = NotificationType.ACHIEVEMENT_UNLOCKED,
                title = "🏆 Conquista Desbloqueada!",
                message = "Mestre da Precisão (+500 XP)",
                timestamp = LocalDateTime.now().minusMinutes(5),
                priority = NotificationPriority.HIGH,
                icon = "🏆"
            ),
            AppNotification(
                id = "notif_2",
                type = NotificationType.LEVEL_UP,
                title = "🎉 Level Up!",
                message = "Parabéns! Você alcançou o nível 13! Novo badge desbloqueado.",
                timestamp = LocalDateTime.now().minusMinutes(30),
                priority = NotificationPriority.HIGH,
                icon = "🎉"
            ),
            AppNotification(
                id = "notif_3",
                type = NotificationType.STREAK_MILESTONE,
                title = "🔥 Sequência Épica!",
                message = "7 dias consecutivos de treino! Você está pegando fogo!",
                timestamp = LocalDateTime.now().minusHours(2),
                priority = NotificationPriority.NORMAL,
                icon = "🔥",
                isRead = true
            ),
            AppNotification(
                id = "notif_4",
                type = NotificationType.DAILY_CHALLENGE,
                title = "🎯 Desafio Quase Completo",
                message = "Faltam apenas 3 drills para completar o desafio de volume!",
                timestamp = LocalDateTime.now().minusHours(1),
                priority = NotificationPriority.NORMAL,
                icon = "🎯"
            ),
            AppNotification(
                id = "notif_5",
                type = NotificationType.SOCIAL_UPDATE,
                title = "👥 Novo Membro no Grupo",
                message = "PokerShark91 entrou no grupo 'Crushers NL200'",
                timestamp = LocalDateTime.now().minusHours(3),
                priority = NotificationPriority.LOW,
                icon = "👥",
                isRead = true
            ),
            AppNotification(
                id = "notif_6",
                type = NotificationType.REWARD_AVAILABLE,
                title = "🎁 Recompensa Disponível",
                message = "Você tem uma recompensa semanal para reivindicar!",
                timestamp = LocalDateTime.now().minusMinutes(45),
                priority = NotificationPriority.NORMAL,
                icon = "🎁"
            ),
            AppNotification(
                id = "notif_7",
                type = NotificationType.TRAINING_REMINDER,
                title = "📚 Hora do Treino",
                message = "Sua sessão de Pós-flop está agendada para agora!",
                timestamp = LocalDateTime.now().minusMinutes(10),
                priority = NotificationPriority.HIGH,
                icon = "📚"
            )
        )
    }
}
