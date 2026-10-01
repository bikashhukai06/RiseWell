package com.example.data.model

data class UserAvatar(
    val presetId: String = "runner",
    val skinToneHex: String = "#FFDBAC",
    val hairStyle: String = "Sporty",
    val hairColorHex: String = "#2D3748",
    val accessory: String = "Headphones",
    val bgGradientStart: String = "#6366F1",
    val bgGradientEnd: String = "#8B5CF6"
)

data class UserProfile(
    val id: String = "user_me",
    val username: String = "Alex Rivers",
    val email: String = "alex.rivers@gmail.com",
    val avatar: UserAvatar = UserAvatar(),
    val currentLevel: Int = 7,
    val currentXp: Int = 350,
    val xpForNextLevel: Int = 500,
    val totalPoints: Int = 2850,
    val currentStreak: Int = 12,
    val bestStreak: Int = 24,
    val completedActivitiesCount: Int = 86,
    val isOAuthed: Boolean = true,
    val authProvider: String = "Google",
    val isGuest: Boolean = false
)

enum class ActivityCategory(val displayName: String, val colorHex: String) {
    MORNING_ALARM("Morning Alarm", "#F59E0B"),
    HYDRATION("Hydration", "#3B82F6"),
    MOVEMENT("Movement", "#10B981"),
    MINDFULNESS("Mindfulness", "#8B5CF6"),
    WIND_DOWN("Wind Down", "#EC4899")
}

data class ActivitySchedule(
    val id: String,
    val title: String,
    val description: String,
    val category: ActivityCategory,
    val hour: Int, // 0 - 23
    val minute: Int, // 0 - 59
    val isAlarmEnabled: Boolean = true,
    val pointsValue: Int = 50,
    val isCompletedToday: Boolean = false,
    val targetDays: String = "Everyday"
) {
    val formattedTime: String
        get() {
            val period = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            return String.format("%02d:%02d %s", displayHour, minute, period)
        }
}

data class CompletedActivity(
    val id: Long = 0,
    val activityScheduleId: String,
    val title: String,
    val category: ActivityCategory,
    val pointsEarned: Int,
    val completedAt: Long,
    val note: String = ""
)

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val category: ActivityCategory,
    val iconEmoji: String,
    val targetValue: Int,
    val currentValue: Int,
    val isUnlocked: Boolean,
    val weekLabel: String = "Week 40",
    val rewardPoints: Int = 100
) {
    val progressFraction: Float
        get() = if (targetValue > 0) (currentValue.toFloat() / targetValue.toFloat()).coerceIn(0f, 1f) else 0f
}

data class FeedPost(
    val id: String,
    val authorName: String,
    val authorAvatar: UserAvatar,
    val timeAgo: String,
    val activityTitle: String,
    val pointsEarned: Int,
    val message: String,
    val likesCount: Int = 0,
    val hasLiked: Boolean = false,
    val commentsCount: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)

data class LeaderboardEntry(
    val rank: Int,
    val username: String,
    val avatar: UserAvatar,
    val points: Int,
    val level: Int,
    val streak: Int,
    val isCurrentUser: Boolean = false,
    val badgeTitle: String = "Consistency Champ"
)
