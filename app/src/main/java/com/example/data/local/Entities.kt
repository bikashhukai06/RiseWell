package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ActivityCategory
import com.example.data.model.UserAvatar

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: String = "user_me",
    val username: String,
    val email: String,
    val avatarPresetId: String,
    val avatarSkinTone: String,
    val avatarHairStyle: String,
    val avatarHairColor: String,
    val avatarAccessory: String,
    val avatarBgStart: String,
    val avatarBgEnd: String,
    val currentLevel: Int,
    val currentXp: Int,
    val xpForNextLevel: Int,
    val totalPoints: Int,
    val currentStreak: Int,
    val bestStreak: Int,
    val completedActivitiesCount: Int,
    val isOAuthed: Boolean,
    val authProvider: String,
    val isGuest: Boolean
) {
    fun toAvatar(): UserAvatar = UserAvatar(
        presetId = avatarPresetId,
        skinToneHex = avatarSkinTone,
        hairStyle = avatarHairStyle,
        hairColorHex = avatarHairColor,
        accessory = avatarAccessory,
        bgGradientStart = avatarBgStart,
        bgGradientEnd = avatarBgEnd
    )
}

@Entity(tableName = "activity_schedules")
data class ActivityScheduleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val hour: Int,
    val minute: Int,
    val isAlarmEnabled: Boolean,
    val pointsValue: Int,
    val isCompletedToday: Boolean,
    val targetDays: String
)

@Entity(tableName = "completed_activities")
data class CompletedActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val activityScheduleId: String,
    val title: String,
    val category: String,
    val pointsEarned: Int,
    val completedAt: Long,
    val note: String
)

@Entity(tableName = "achievement_badges")
data class AchievementBadgeEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String,
    val iconEmoji: String,
    val targetValue: Int,
    val currentValue: Int,
    val isUnlocked: Boolean,
    val weekLabel: String,
    val rewardPoints: Int
)

@Entity(tableName = "feed_posts")
data class FeedPostEntity(
    @PrimaryKey val id: String,
    val authorName: String,
    val authorAvatarPreset: String,
    val authorAvatarSkin: String,
    val timeAgo: String,
    val activityTitle: String,
    val pointsEarned: Int,
    val message: String,
    val likesCount: Int,
    val hasLiked: Boolean,
    val commentsCount: Int,
    val timestamp: Long
)

@Entity(tableName = "leaderboard_entries")
data class LeaderboardEntity(
    @PrimaryKey val rank: Int,
    val username: String,
    val avatarPreset: String,
    val avatarSkin: String,
    val points: Int,
    val level: Int,
    val streak: Int,
    val isCurrentUser: Boolean,
    val badgeTitle: String
)
