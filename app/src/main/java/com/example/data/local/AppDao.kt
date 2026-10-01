package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // User Profile
    @Query("SELECT * FROM user_profile WHERE id = 'user_me'")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    // Activity Schedules / Alarms
    @Query("SELECT * FROM activity_schedules ORDER BY hour ASC, minute ASC")
    fun getAllSchedules(): Flow<List<ActivityScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedules(schedules: List<ActivityScheduleEntity>)

    @Update
    suspend fun updateSchedule(schedule: ActivityScheduleEntity)

    @Query("UPDATE activity_schedules SET isCompletedToday = :completed WHERE id = :id")
    suspend fun updateScheduleCompletion(id: String, completed: Boolean)

    @Query("UPDATE activity_schedules SET hour = :hour, minute = :minute, isAlarmEnabled = :isAlarmEnabled WHERE id = :id")
    suspend fun updateScheduleTime(id: String, hour: Int, minute: Int, isAlarmEnabled: Boolean)

    // Completed Activities History
    @Query("SELECT * FROM completed_activities ORDER BY completedAt DESC")
    fun getAllCompletedActivities(): Flow<List<CompletedActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletedActivity(activity: CompletedActivityEntity)

    @Query("DELETE FROM completed_activities")
    suspend fun clearHistory()

    // Achievement Badges
    @Query("SELECT * FROM achievement_badges ORDER BY isUnlocked DESC, targetValue ASC")
    fun getAllBadges(): Flow<List<AchievementBadgeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBadges(badges: List<AchievementBadgeEntity>)

    @Query("UPDATE achievement_badges SET currentValue = :value, isUnlocked = :unlocked WHERE id = :id")
    suspend fun updateBadgeProgress(id: String, value: Int, unlocked: Boolean)

    // Feed Posts
    @Query("SELECT * FROM feed_posts ORDER BY timestamp DESC")
    fun getAllFeedPosts(): Flow<List<FeedPostEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedPost(post: FeedPostEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeedPosts(posts: List<FeedPostEntity>)

    @Query("UPDATE feed_posts SET likesCount = :likes, hasLiked = :hasLiked WHERE id = :id")
    suspend fun updatePostLike(id: String, likes: Int, hasLiked: Boolean)

    // Leaderboard
    @Query("SELECT * FROM leaderboard_entries ORDER BY rank ASC")
    fun getLeaderboard(): Flow<List<LeaderboardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboard(entries: List<LeaderboardEntity>)
}
