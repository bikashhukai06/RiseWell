package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.notification.NotificationHelper
import com.example.ui.components.CelebrationDialog
import com.example.ui.screens.*
import com.example.ui.theme.RiseWellTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.WellnessViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)

        setContent {
            val context = LocalContext.current
            val viewModel: WellnessViewModel = viewModel(factory = WellnessViewModel.Factory(context))

            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val schedules by viewModel.schedules.collectAsStateWithLifecycle()
            val completedActivities by viewModel.completedActivities.collectAsStateWithLifecycle()
            val badges by viewModel.badges.collectAsStateWithLifecycle()
            val feedPosts by viewModel.feedPosts.collectAsStateWithLifecycle()
            val leaderboard by viewModel.leaderboard.collectAsStateWithLifecycle()
            val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
            val celebrationEvent by viewModel.celebrationEvent.collectAsStateWithLifecycle()
            val userDarkMode by viewModel.isDarkMode.collectAsStateWithLifecycle()
            val notificationsEnabled by viewModel.notificationsEnabled.collectAsStateWithLifecycle()

            val effectiveDarkMode = userDarkMode ?: isSystemInDarkTheme()

            RiseWellTheme(darkTheme = effectiveDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainAppScaffold(
                        viewModel = viewModel,
                        currentTab = currentTab,
                        userProfile = userProfile,
                        schedules = schedules,
                        completedActivities = completedActivities,
                        badges = badges,
                        feedPosts = feedPosts,
                        leaderboard = leaderboard,
                        isDarkMode = userDarkMode,
                        notificationsEnabled = notificationsEnabled
                    )

                    celebrationEvent?.let { event ->
                        CelebrationDialog(
                            event = event,
                            onDismiss = { viewModel.dismissCelebration() },
                            onShare = { title, points, message ->
                                viewModel.shareAchievement(title, points, message)
                            }
                        )
                    }
                }
            }
        }
    }
}

data class NavItem(
    val tab: AppTab,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String,
    val tag: String
)

@Composable
fun MainAppScaffold(
    viewModel: WellnessViewModel,
    currentTab: AppTab,
    userProfile: com.example.data.model.UserProfile?,
    schedules: List<com.example.data.model.ActivitySchedule>,
    completedActivities: List<com.example.data.model.CompletedActivity>,
    badges: List<com.example.data.model.AchievementBadge>,
    feedPosts: List<com.example.data.model.FeedPost>,
    leaderboard: List<com.example.data.model.LeaderboardEntry>,
    isDarkMode: Boolean?,
    notificationsEnabled: Boolean
) {
    // Back navigation support
    if (currentTab != AppTab.PROFILE) {
        BackHandler {
            viewModel.selectTab(AppTab.PROFILE)
        }
    }

    val navItems = listOf(
        NavItem(AppTab.PROFILE, Icons.Default.Person, Icons.Outlined.Person, "Profile", "nav_profile"),
        NavItem(AppTab.ACTIVITIES, Icons.Default.Alarm, Icons.Outlined.Alarm, "Alarms", "nav_alarms"),
        NavItem(AppTab.PROGRESS, Icons.Default.BarChart, Icons.Outlined.BarChart, "Growth", "nav_progress"),
        NavItem(AppTab.COMMUNITY, Icons.Default.EmojiEvents, Icons.Outlined.EmojiEvents, "Social", "nav_social"),
        NavItem(AppTab.SETTINGS, Icons.Default.Settings, Icons.Outlined.Settings, "Settings", "nav_settings")
    )

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth >= 600.dp

        if (isWideScreen) {
            // Adaptive Tablet Layout: Side Navigation Rail
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail(
                    modifier = Modifier.testTag("tablet_nav_rail"),
                    header = {
                        Icon(
                            imageVector = Icons.Default.SelfImprovement,
                            contentDescription = "RiseWell",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(vertical = 12.dp)
                                .size(32.dp)
                        )
                    }
                ) {
                    navItems.forEach { item ->
                        val isSelected = currentTab == item.tab
                        NavigationRailItem(
                            selected = isSelected,
                            onClick = { viewModel.selectTab(item.tab) },
                            icon = {
                                Icon(
                                    imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.label
                                )
                            },
                            label = { Text(item.label) },
                            modifier = Modifier.testTag(item.tag)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                ) {
                    TabContent(
                        currentTab = currentTab,
                        viewModel = viewModel,
                        userProfile = userProfile,
                        schedules = schedules,
                        completedActivities = completedActivities,
                        badges = badges,
                        feedPosts = feedPosts,
                        leaderboard = leaderboard,
                        isDarkMode = isDarkMode,
                        notificationsEnabled = notificationsEnabled
                    )
                }
            }
        } else {
            // Mobile Phone Layout: Bottom Navigation Bar
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                bottomBar = {
                    NavigationBar(
                        modifier = Modifier
                            .testTag("bottom_nav_bar")
                            .windowInsetsPadding(WindowInsets.navigationBars)
                    ) {
                        navItems.forEach { item ->
                            val isSelected = currentTab == item.tab
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = { viewModel.selectTab(item.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                        contentDescription = item.label
                                    )
                                },
                                label = { Text(item.label) },
                                modifier = Modifier.testTag(item.tag)
                            )
                        }
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    TabContent(
                        currentTab = currentTab,
                        viewModel = viewModel,
                        userProfile = userProfile,
                        schedules = schedules,
                        completedActivities = completedActivities,
                        badges = badges,
                        feedPosts = feedPosts,
                        leaderboard = leaderboard,
                        isDarkMode = isDarkMode,
                        notificationsEnabled = notificationsEnabled
                    )
                }
            }
        }
    }
}

@Composable
fun TabContent(
    currentTab: AppTab,
    viewModel: WellnessViewModel,
    userProfile: com.example.data.model.UserProfile?,
    schedules: List<com.example.data.model.ActivitySchedule>,
    completedActivities: List<com.example.data.model.CompletedActivity>,
    badges: List<com.example.data.model.AchievementBadge>,
    feedPosts: List<com.example.data.model.FeedPost>,
    leaderboard: List<com.example.data.model.LeaderboardEntry>,
    isDarkMode: Boolean?,
    notificationsEnabled: Boolean
) {
    when (currentTab) {
        AppTab.PROFILE -> {
            ProfileScreen(
                userProfile = userProfile,
                badges = badges,
                history = completedActivities,
                onUpdateAvatar = { viewModel.updateAvatar(it) },
                onUpdateUsername = { viewModel.updateUsername(it) },
                onShareAchievement = { title, points, msg ->
                    viewModel.shareAchievement(title, points, msg)
                },
                onUpdateOAuth = { isOAuthed, provider, email, username ->
                    viewModel.updateOAuthAccount(isOAuthed, provider, email, username)
                }
            )
        }
        AppTab.ACTIVITIES -> {
            DailyActivitiesScreen(
                schedules = schedules,
                onCompleteActivity = { ctx, id, note ->
                    viewModel.completeActivity(ctx, id, note)
                },
                onToggleAlarm = { id, hour, minute, enabled ->
                    viewModel.updateScheduleTiming(id, hour, minute, enabled)
                },
                onOpenSettings = {
                    viewModel.selectTab(AppTab.SETTINGS)
                }
            )
        }
        AppTab.PROGRESS -> {
            ProgressDashboardScreen(
                userProfile = userProfile
            )
        }
        AppTab.COMMUNITY -> {
            SocialFeedScreen(
                userProfile = userProfile,
                feedPosts = feedPosts,
                leaderboard = leaderboard,
                onToggleLike = { viewModel.toggleLikePost(it) },
                onPublishPost = { title, points, msg ->
                    viewModel.shareAchievement(title, points, msg)
                }
            )
        }
        AppTab.SETTINGS -> {
            SettingsScreen(
                userProfile = userProfile,
                schedules = schedules,
                isDarkMode = isDarkMode,
                notificationsEnabled = notificationsEnabled,
                onSetDarkMode = { viewModel.setDarkMode(it) },
                onSetNotifications = { viewModel.setNotificationsEnabled(it) },
                onUpdateScheduleTime = { id, hour, minute, enabled ->
                    viewModel.updateScheduleTiming(id, hour, minute, enabled)
                },
                onTestNotification = { ctx ->
                    viewModel.testPushNotification(ctx)
                },
                onUpdateOAuth = { isOAuthed, provider, email, username ->
                    viewModel.updateOAuthAccount(isOAuthed, provider, email, username)
                }
            )
        }
    }
}
