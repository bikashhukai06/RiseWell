package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.util.UUID

class WellnessRepository(private val dao: AppDao) {

    val userProfile: Flow<UserProfile?> = dao.getUserProfile().map { entity ->
        entity?.let {
            UserProfile(
                id = it.id,
                username = it.username,
                email = it.email,
                avatar = it.toAvatar(),
                currentLevel = it.currentLevel,
                currentXp = it.currentXp,
                xpForNextLevel = it.xpForNextLevel,
                totalPoints = it.totalPoints,
                currentStreak = it.currentStreak,
                bestStreak = it.bestStreak,
                completedActivitiesCount = it.completedActivitiesCount,
                isOAuthed = it.isOAuthed,
                authProvider = it.authProvider,
                isGuest = it.isGuest
            )
        }
    }

    val schedules: Flow<List<ActivitySchedule>> = dao.getAllSchedules().map { list ->
        list.map {
            ActivitySchedule(
                id = it.id,
                title = it.title,
                description = it.description,
                category = parseCategory(it.category),
                hour = it.hour,
                minute = it.minute,
                isAlarmEnabled = it.isAlarmEnabled,
                pointsValue = it.pointsValue,
                isCompletedToday = it.isCompletedToday,
                targetDays = it.targetDays
            )
        }
    }

    val completedActivities: Flow<List<CompletedActivity>> = dao.getAllCompletedActivities().map { list ->
        list.map {
            CompletedActivity(
                id = it.id,
                activityScheduleId = it.activityScheduleId,
                title = it.title,
                category = parseCategory(it.category),
                pointsEarned = it.pointsEarned,
                completedAt = it.completedAt,
                note = it.note
            )
        }
    }

    val badges: Flow<List<AchievementBadge>> = dao.getAllBadges().map { list ->
        list.map {
            AchievementBadge(
                id = it.id,
                title = it.title,
                description = it.description,
                category = parseCategory(it.category),
                iconEmoji = it.iconEmoji,
                targetValue = it.targetValue,
                currentValue = it.currentValue,
                isUnlocked = it.isUnlocked,
                weekLabel = it.weekLabel,
                rewardPoints = it.rewardPoints
            )
        }
    }

    val feedPosts: Flow<List<FeedPost>> = dao.getAllFeedPosts().map { list ->
        list.map {
            FeedPost(
                id = it.id,
                authorName = it.authorName,
                authorAvatar = UserAvatar(presetId = it.authorAvatarPreset, skinToneHex = it.authorAvatarSkin),
                timeAgo = it.timeAgo,
                activityTitle = it.activityTitle,
                pointsEarned = it.pointsEarned,
                message = it.message,
                likesCount = it.likesCount,
                hasLiked = it.hasLiked,
                commentsCount = it.commentsCount,
                timestamp = it.timestamp
            )
        }
    }

    val leaderboard: Flow<List<LeaderboardEntry>> = dao.getLeaderboard().map { list ->
        list.map {
            LeaderboardEntry(
                rank = it.rank,
                username = it.username,
                avatar = UserAvatar(presetId = it.avatarPreset, skinToneHex = it.avatarSkin),
                points = it.points,
                level = it.level,
                streak = it.streak,
                isCurrentUser = it.isCurrentUser,
                badgeTitle = it.badgeTitle
            )
        }
    }

    suspend fun seedInitialDataIfEmpty() {
        val existingProfile = dao.getUserProfile().firstOrNull()
        if (existingProfile == null) {
            // Seed Profile
            val initialProfile = UserProfileEntity(
                id = "user_me",
                username = "Alex Rivers",
                email = "alex.rivers@gmail.com",
                avatarPresetId = "runner",
                avatarSkinTone = "#FFDBAC",
                avatarHairStyle = "Sporty",
                avatarHairColor = "#3B82F6",
                avatarAccessory = "Headphones",
                avatarBgStart = "#6366F1",
                avatarBgEnd = "#8B5CF6",
                currentLevel = 7,
                currentXp = 350,
                xpForNextLevel = 500,
                totalPoints = 2850,
                currentStreak = 12,
                bestStreak = 24,
                completedActivitiesCount = 86,
                isOAuthed = true,
                authProvider = "Google",
                isGuest = false
            )
            dao.insertOrUpdateProfile(initialProfile)

            // Seed Schedules
            val initialSchedules = listOf(
                ActivityScheduleEntity(
                    id = "act_morning_alarm",
                    title = "Rise & Shine Alarm + Stretch",
                    description = "Wake up on first alarm bell and complete 5-min mobility stretch",
                    category = ActivityCategory.MORNING_ALARM.name,
                    hour = 7,
                    minute = 0,
                    isAlarmEnabled = true,
                    pointsValue = 60,
                    isCompletedToday = true,
                    targetDays = "Everyday"
                ),
                ActivityScheduleEntity(
                    id = "act_hydration",
                    title = "Hydration Kickstart (500ml)",
                    description = "Drink a large glass of water with lemon to activate metabolism",
                    category = ActivityCategory.HYDRATION.name,
                    hour = 8,
                    minute = 30,
                    isAlarmEnabled = true,
                    pointsValue = 30,
                    isCompletedToday = true,
                    targetDays = "Mon - Fri"
                ),
                ActivityScheduleEntity(
                    id = "act_posture_walk",
                    title = "Midday Posture & Mobility",
                    description = "Step away from screen: 10 min brisk walk + spinal mobility",
                    category = ActivityCategory.MOVEMENT.name,
                    hour = 12,
                    minute = 30,
                    isAlarmEnabled = true,
                    pointsValue = 45,
                    isCompletedToday = false,
                    targetDays = "Mon - Fri"
                ),
                ActivityScheduleEntity(
                    id = "act_afternoon_workout",
                    title = "Core & Cardio Blast",
                    description = "30-min targeted strength or interval run to hit peak heart rate",
                    category = ActivityCategory.MOVEMENT.name,
                    hour = 17,
                    minute = 30,
                    isAlarmEnabled = true,
                    pointsValue = 75,
                    isCompletedToday = false,
                    targetDays = "Mon, Wed, Fri"
                ),
                ActivityScheduleEntity(
                    id = "act_bedtime_winddown",
                    title = "Bedtime Alarm & Breathing",
                    description = "Turn off blue screens, dim lights and practice 4-7-8 breathwork",
                    category = ActivityCategory.WIND_DOWN.name,
                    hour = 21,
                    minute = 30,
                    isAlarmEnabled = true,
                    pointsValue = 40,
                    isCompletedToday = false,
                    targetDays = "Everyday"
                )
            )
            dao.insertSchedules(initialSchedules)

            // Seed Completed Activities History
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L
            val initialHistory = listOf(
                CompletedActivityEntity(
                    activityScheduleId = "act_morning_alarm",
                    title = "Rise & Shine Alarm + Stretch",
                    category = ActivityCategory.MORNING_ALARM.name,
                    pointsEarned = 60,
                    completedAt = now - (3 * 3600 * 1000L),
                    note = "Woke up energized before alarm ring! Clean 5 min routine."
                ),
                CompletedActivityEntity(
                    activityScheduleId = "act_hydration",
                    title = "Hydration Kickstart (500ml)",
                    category = ActivityCategory.HYDRATION.name,
                    pointsEarned = 30,
                    completedAt = now - (2 * 3600 * 1000L),
                    note = "Cold lemon water logged."
                ),
                CompletedActivityEntity(
                    activityScheduleId = "act_bedtime_winddown",
                    title = "Bedtime Alarm & Breathing",
                    category = ActivityCategory.WIND_DOWN.name,
                    pointsEarned = 40,
                    completedAt = now - dayMillis + (2 * 3600 * 1000L),
                    note = "Deep restful sleep, asleep by 10:15 PM."
                ),
                CompletedActivityEntity(
                    activityScheduleId = "act_afternoon_workout",
                    title = "Core & Cardio Blast",
                    category = ActivityCategory.MOVEMENT.name,
                    pointsEarned = 75,
                    completedAt = now - dayMillis - (4 * 3600 * 1000L),
                    note = "5km trail run, felt amazing pace!"
                ),
                CompletedActivityEntity(
                    activityScheduleId = "act_posture_walk",
                    title = "Midday Posture & Mobility",
                    category = ActivityCategory.MOVEMENT.name,
                    pointsEarned = 45,
                    completedAt = now - (2 * dayMillis),
                    note = "Desk stretches done."
                ),
                CompletedActivityEntity(
                    activityScheduleId = "act_morning_alarm",
                    title = "Rise & Shine Alarm + Stretch",
                    category = ActivityCategory.MORNING_ALARM.name,
                    pointsEarned = 60,
                    completedAt = now - (2 * dayMillis) - (6 * 3600 * 1000L),
                    note = "On time wakeup."
                )
            )
            initialHistory.forEach { dao.insertCompletedActivity(it) }

            // Seed Weekly Badges
            val initialBadges = listOf(
                AchievementBadgeEntity(
                    id = "badge_early_bird",
                    title = "Early Bird Master",
                    description = "Wake up and dismiss the morning alarm on time 7 days in a row",
                    category = ActivityCategory.MORNING_ALARM.name,
                    iconEmoji = "⏰",
                    targetValue = 7,
                    currentValue = 7,
                    isUnlocked = true,
                    weekLabel = "Week 40",
                    rewardPoints = 150
                ),
                AchievementBadgeEntity(
                    id = "badge_hydration_titan",
                    title = "Hydration Titan",
                    description = "Complete morning hydration protocol 6 out of 7 days this week",
                    category = ActivityCategory.HYDRATION.name,
                    iconEmoji = "💧",
                    targetValue = 6,
                    currentValue = 5,
                    isUnlocked = false,
                    weekLabel = "Week 40",
                    rewardPoints = 100
                ),
                AchievementBadgeEntity(
                    id = "badge_zen_warrior",
                    title = "Zen Routine",
                    description = "Complete 5 evening wind-down meditations before sleep",
                    category = ActivityCategory.WIND_DOWN.name,
                    iconEmoji = "🧘",
                    targetValue = 5,
                    currentValue = 5,
                    isUnlocked = true,
                    weekLabel = "Week 40",
                    rewardPoints = 120
                ),
                AchievementBadgeEntity(
                    id = "badge_streak_sentinel",
                    title = "Streak Dynamo",
                    description = "Maintain active daily habit streak for 14 continuous days",
                    category = ActivityCategory.MOVEMENT.name,
                    iconEmoji = "🔥",
                    targetValue = 14,
                    currentValue = 12,
                    isUnlocked = false,
                    weekLabel = "Week 40",
                    rewardPoints = 200
                ),
                AchievementBadgeEntity(
                    id = "badge_movement_crusher",
                    title = "Movement Crusher",
                    description = "Hit 4 high-energy afternoon workouts in the current week",
                    category = ActivityCategory.MOVEMENT.name,
                    iconEmoji = "⚡",
                    targetValue = 4,
                    currentValue = 4,
                    isUnlocked = true,
                    weekLabel = "Week 40",
                    rewardPoints = 180
                ),
                AchievementBadgeEntity(
                    id = "badge_social_spark",
                    title = "Community Spark",
                    description = "Share 3 completed wellness milestones with friends on social feed",
                    category = ActivityCategory.MINDFULNESS.name,
                    iconEmoji = "🌟",
                    targetValue = 3,
                    currentValue = 3,
                    isUnlocked = true,
                    weekLabel = "Week 40",
                    rewardPoints = 100
                )
            )
            dao.insertBadges(initialBadges)

            // Seed Feed Posts
            val initialPosts = listOf(
                FeedPostEntity(
                    id = "post_1",
                    authorName = "Elena Vance",
                    authorAvatarPreset = "champion",
                    authorAvatarSkin = "#F1C27D",
                    timeAgo = "25m ago",
                    activityTitle = "Morning Rise & Shine Alarm + Stretch",
                    pointsEarned = 60,
                    message = "Alarm went off at 6:30 AM and got straight into sun salutations! 7-day streak locked in 🌅",
                    likesCount = 14,
                    hasLiked = true,
                    commentsCount = 3,
                    timestamp = now - 25 * 60 * 1000L
                ),
                FeedPostEntity(
                    id = "post_2",
                    authorName = "Alex Rivers",
                    authorAvatarPreset = "runner",
                    authorAvatarSkin = "#FFDBAC",
                    timeAgo = "3h ago",
                    activityTitle = "Rise & Shine Alarm + Stretch",
                    pointsEarned = 60,
                    message = "Crushed my morning alarm! Energy levels at 100% today. Consistency is compounding 💪",
                    likesCount = 9,
                    hasLiked = false,
                    commentsCount = 2,
                    timestamp = now - 3 * 3600 * 1000L
                ),
                FeedPostEntity(
                    id = "post_3",
                    authorName = "Marcus Chen",
                    authorAvatarPreset = "yogi",
                    authorAvatarSkin = "#E0AC69",
                    timeAgo = "5h ago",
                    activityTitle = "Core & Cardio Blast",
                    pointsEarned = 75,
                    message = "Beat my personal best on the 5k interval session! +75 points pushing towards Level 8!",
                    likesCount = 22,
                    hasLiked = true,
                    commentsCount = 5,
                    timestamp = now - 5 * 3600 * 1000L
                ),
                FeedPostEntity(
                    id = "post_4",
                    authorName = "Sophia Lin",
                    authorAvatarPreset = "zen",
                    authorAvatarSkin = "#FFDBAC",
                    timeAgo = "1d ago",
                    activityTitle = "Bedtime Alarm & Breathing",
                    pointsEarned = 40,
                    message = "10 minutes of 4-7-8 breathing before sleep. Best restorative rest this week ✨",
                    likesCount = 18,
                    hasLiked = false,
                    commentsCount = 1,
                    timestamp = now - dayMillis
                )
            )
            dao.insertFeedPosts(initialPosts)

            // Seed Leaderboard
            val initialLeaderboard = listOf(
                LeaderboardEntity(1, "Elena Vance", "champion", "#F1C27D", 3420, 9, 21, false, "Diamond League"),
                LeaderboardEntity(2, "Marcus Chen", "yogi", "#E0AC69", 3180, 8, 18, false, "Diamond League"),
                LeaderboardEntity(3, "Sophia Lin", "zen", "#FFDBAC", 2990, 8, 15, false, "Platinum League"),
                LeaderboardEntity(4, "Alex Rivers", "runner", "#FFDBAC", 2850, 7, 12, true, "Platinum League"),
                LeaderboardEntity(5, "Jordan Taylor", "cyber", "#C68642", 2640, 7, 9, false, "Platinum League"),
                LeaderboardEntity(6, "Maya Patel", "adventurer", "#8D5524", 2410, 6, 7, false, "Gold League"),
                LeaderboardEntity(7, "Liam Scott", "runner", "#FFDBAC", 2190, 6, 5, false, "Gold League"),
                LeaderboardEntity(8, "Chloe Dupuis", "champion", "#F1C27D", 1980, 5, 4, false, "Gold League")
            )
            dao.insertLeaderboard(initialLeaderboard)
        }
    }

    suspend fun updateAvatar(avatar: UserAvatar) {
        val current = dao.getUserProfile().firstOrNull() ?: return
        val updated = current.copy(
            avatarPresetId = avatar.presetId,
            avatarSkinTone = avatar.skinToneHex,
            avatarHairStyle = avatar.hairStyle,
            avatarHairColor = avatar.hairColorHex,
            avatarAccessory = avatar.accessory,
            avatarBgStart = avatar.bgGradientStart,
            avatarBgEnd = avatar.bgGradientEnd
        )
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun updateUsername(newUsername: String) {
        val current = dao.getUserProfile().firstOrNull() ?: return
        val updated = current.copy(username = newUsername.trim())
        dao.insertOrUpdateProfile(updated)
    }

    suspend fun completeActivity(scheduleId: String, note: String = ""): CompletedActivityResult {
        val schedulesList = dao.getAllSchedules().firstOrNull() ?: emptyList()
        val targetSchedule = schedulesList.find { it.id == scheduleId } ?: return CompletedActivityResult(false)

        dao.updateScheduleCompletion(scheduleId, true)

        val completedItem = CompletedActivityEntity(
            activityScheduleId = scheduleId,
            title = targetSchedule.title,
            category = targetSchedule.category,
            pointsEarned = targetSchedule.pointsValue,
            completedAt = System.currentTimeMillis(),
            note = note.ifBlank { "Completed on schedule" }
        )
        dao.insertCompletedActivity(completedItem)

        // Update profile points, XP and streak
        var leveledUp = false
        var newLevel = 1
        val currentProfile = dao.getUserProfile().firstOrNull()
        if (currentProfile != null) {
            val addedPoints = targetSchedule.pointsValue
            var newTotalPoints = currentProfile.totalPoints + addedPoints
            var newXp = currentProfile.currentXp + addedPoints
            var lvl = currentProfile.currentLevel
            var xpNeeded = currentProfile.xpForNextLevel

            while (newXp >= xpNeeded) {
                newXp -= xpNeeded
                lvl += 1
                xpNeeded = (lvl * 75) + 200
                leveledUp = true
            }
            newLevel = lvl

            val updatedProfile = currentProfile.copy(
                totalPoints = newTotalPoints,
                currentXp = newXp,
                currentLevel = lvl,
                xpForNextLevel = xpNeeded,
                completedActivitiesCount = currentProfile.completedActivitiesCount + 1
            )
            dao.insertOrUpdateProfile(updatedProfile)
        }

        return CompletedActivityResult(
            success = true,
            pointsEarned = targetSchedule.pointsValue,
            leveledUp = leveledUp,
            newLevel = newLevel,
            activityTitle = targetSchedule.title
        )
    }

    suspend fun updateScheduleTime(id: String, hour: Int, minute: Int, isAlarmEnabled: Boolean) {
        dao.updateScheduleTime(id, hour, minute, isAlarmEnabled)
    }

    suspend fun toggleLikePost(postId: String) {
        val posts = dao.getAllFeedPosts().firstOrNull() ?: return
        val post = posts.find { it.id == postId } ?: return
        val newHasLiked = !post.hasLiked
        val newLikes = if (newHasLiked) post.likesCount + 1 else (post.likesCount - 1).coerceAtLeast(0)
        dao.updatePostLike(postId, newLikes, newHasLiked)
    }

    suspend fun publishFeedPost(activityTitle: String, pointsEarned: Int, message: String) {
        val profile = dao.getUserProfile().firstOrNull()
        val post = FeedPostEntity(
            id = UUID.randomUUID().toString(),
            authorName = profile?.username ?: "Alex Rivers",
            authorAvatarPreset = profile?.avatarPresetId ?: "runner",
            authorAvatarSkin = profile?.avatarSkinTone ?: "#FFDBAC",
            timeAgo = "Just now",
            activityTitle = activityTitle,
            pointsEarned = pointsEarned,
            message = message,
            likesCount = 1,
            hasLiked = true,
            commentsCount = 0,
            timestamp = System.currentTimeMillis()
        )
        dao.insertFeedPost(post)
    }

    suspend fun updateOAuthAccount(
        isOAuthed: Boolean,
        provider: String,
        email: String,
        username: String
    ) {
        val current = dao.getUserProfile().firstOrNull() ?: return
        val updated = current.copy(
            isOAuthed = isOAuthed,
            authProvider = provider,
            email = email,
            username = username,
            isGuest = !isOAuthed
        )
        dao.insertOrUpdateProfile(updated)
    }

    private fun parseCategory(cat: String): ActivityCategory {
        return try {
            ActivityCategory.valueOf(cat)
        } catch (e: Exception) {
            ActivityCategory.MOVEMENT
        }
    }
}

data class CompletedActivityResult(
    val success: Boolean,
    val pointsEarned: Int = 0,
    val leveledUp: Boolean = false,
    val newLevel: Int = 0,
    val activityTitle: String = ""
)
