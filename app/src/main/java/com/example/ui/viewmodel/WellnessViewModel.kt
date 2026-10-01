package com.example.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.model.*
import com.example.data.repository.CompletedActivityResult
import com.example.data.repository.WellnessRepository
import com.example.notification.NotificationHelper
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppTab(val title: String) {
    PROFILE("Profile"),
    ACTIVITIES("Alarms & Habits"),
    PROGRESS("Dashboard"),
    COMMUNITY("Social & Ranks"),
    SETTINGS("Settings")
}

data class CelebrationEvent(
    val title: String,
    val message: String,
    val points: Int,
    val isLevelUp: Boolean = false,
    val newLevel: Int = 1
)

class WellnessViewModel(private val repository: WellnessRepository) : ViewModel() {

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val schedules: StateFlow<List<ActivitySchedule>> = repository.schedules
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val completedActivities: StateFlow<List<CompletedActivity>> = repository.completedActivities
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val badges: StateFlow<List<AchievementBadge>> = repository.badges
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val feedPosts: StateFlow<List<FeedPost>> = repository.feedPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<LeaderboardEntry>> = repository.leaderboard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentTab = MutableStateFlow(AppTab.PROFILE)
    val currentTab: StateFlow<AppTab> = _currentTab.asStateFlow()

    private val _celebrationEvent = MutableStateFlow<CelebrationEvent?>(null)
    val celebrationEvent: StateFlow<CelebrationEvent?> = _celebrationEvent.asStateFlow()

    private val _isDarkMode = MutableStateFlow<Boolean?>(null) // null = system default
    val isDarkMode: StateFlow<Boolean?> = _isDarkMode.asStateFlow()

    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun selectTab(tab: AppTab) {
        _currentTab.value = tab
    }

    fun setDarkMode(enabled: Boolean?) {
        _isDarkMode.value = enabled
    }

    fun setNotificationsEnabled(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun dismissCelebration() {
        _celebrationEvent.value = null
    }

    fun completeActivity(context: Context, scheduleId: String, note: String = "") {
        viewModelScope.launch {
            val result: CompletedActivityResult = repository.completeActivity(scheduleId, note)
            if (result.success) {
                _celebrationEvent.value = CelebrationEvent(
                    title = if (result.leveledUp) "🎉 LEVEL UP: LEVEL ${result.newLevel}!" else "🌟 Habit Crushed!",
                    message = if (result.leveledUp)
                        "Incredible discipline! You reached Level ${result.newLevel} and earned +${result.pointsEarned} pts!"
                    else
                        "You completed '${result.activityTitle}' and earned +${result.pointsEarned} pts!",
                    points = result.pointsEarned,
                    isLevelUp = result.leveledUp,
                    newLevel = result.newLevel
                )

                if (_notificationsEnabled.value) {
                    NotificationHelper.sendWellnessNotification(
                        context = context,
                        notificationId = scheduleId.hashCode(),
                        title = "Habit Completed: ${result.activityTitle}",
                        message = "Awesome progress! +${result.pointsEarned} pts logged.",
                        pointsInfo = "Streak continues!"
                    )
                }
            }
        }
    }

    fun updateAvatar(avatar: UserAvatar) {
        viewModelScope.launch {
            repository.updateAvatar(avatar)
        }
    }

    fun updateUsername(newName: String) {
        viewModelScope.launch {
            repository.updateUsername(newName)
        }
    }

    fun updateScheduleTiming(id: String, hour: Int, minute: Int, isAlarmEnabled: Boolean) {
        viewModelScope.launch {
            repository.updateScheduleTime(id, hour, minute, isAlarmEnabled)
        }
    }

    fun toggleLikePost(postId: String) {
        viewModelScope.launch {
            repository.toggleLikePost(postId)
        }
    }

    fun shareAchievement(activityTitle: String, points: Int, message: String) {
        viewModelScope.launch {
            repository.publishFeedPost(activityTitle, points, message)
        }
    }

    fun updateOAuthAccount(isOAuthed: Boolean, provider: String, email: String, username: String) {
        viewModelScope.launch {
            repository.updateOAuthAccount(isOAuthed, provider, email, username)
        }
    }

    fun testPushNotification(context: Context): Boolean {
        return NotificationHelper.sendWellnessNotification(
            context = context,
            notificationId = 1001,
            title = "⏰ RiseWell Alarm Alert",
            message = "Time for: Rise & Shine Alarm + Stretch. Tap to claim +60 pts!",
            pointsInfo = "+60 pts"
        )
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val db = AppDatabase.getInstance(context)
            val repo = WellnessRepository(db.appDao())
            return WellnessViewModel(repo) as T
        }
    }
}
