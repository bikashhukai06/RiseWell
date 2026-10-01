package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AchievementBadge
import com.example.data.model.ActivityCategory
import com.example.data.model.CompletedActivity
import com.example.data.model.UserProfile
import com.example.ui.components.AvatarCustomizerDialog
import com.example.ui.components.AvatarView
import com.example.ui.components.BadgeDetailDialog
import com.example.ui.components.OAuthAccountDialog
import com.example.ui.components.parseColor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userProfile: UserProfile?,
    badges: List<AchievementBadge>,
    history: List<CompletedActivity>,
    onUpdateAvatar: (com.example.data.model.UserAvatar) -> Unit,
    onUpdateUsername: (String) -> Unit,
    onShareAchievement: (title: String, points: Int, message: String) -> Unit,
    onUpdateOAuth: (isOAuthed: Boolean, provider: String, email: String, username: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAvatarCustomizer by remember { mutableStateOf(false) }
    var showOAuthDialog by remember { mutableStateOf(false) }
    var selectedBadgeForDetail by remember { mutableStateOf<AchievementBadge?>(null) }
    var showEditNameDialog by remember { mutableStateOf(false) }
    var tempName by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf<ActivityCategory?>(null) }

    val filteredHistory = remember(history, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) history
        else history.filter { it.category == selectedCategoryFilter }
    }

    Scaffold(
        modifier = modifier.testTag("profile_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Player Profile",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                actions = {
                    IconButton(
                        onClick = { showOAuthDialog = true },
                        modifier = Modifier.testTag("profile_oauth_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "OAuth Account",
                            tint = if (userProfile?.isOAuthed == true) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Profile Card with Avatar, Level, Points, and Customizer Button
            item {
                ProfileHeroCard(
                    userProfile = userProfile,
                    onCustomizeAvatar = { showAvatarCustomizer = true },
                    onEditName = {
                        tempName = userProfile?.username ?: ""
                        showEditNameDialog = true
                    },
                    onOpenOAuth = { showOAuthDialog = true }
                )
            }

            // Stats Quick Row (Total Points, Streak, Best Streak, Completed)
            item {
                StatsGrid(userProfile = userProfile)
            }

            // Interactive Weekly Achievement Badges Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Weekly Achievement Badges",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Tap any badge to inspect progress & share",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    val unlockedCount = badges.count { it.isUnlocked }
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "$unlockedCount / ${badges.size} Badges",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Badges Horizontal Flow / Carousel
            item {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("badges_list")
                ) {
                    items(badges) { badge ->
                        BadgeCard(
                            badge = badge,
                            onClick = { selectedBadgeForDetail = badge }
                        )
                    }
                }
            }

            // Completed Activities History Header & Filter
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Completed Activities History",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${filteredHistory.size} logged",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedCategoryFilter == null,
                                onClick = { selectedCategoryFilter = null },
                                label = { Text("All") },
                                modifier = Modifier.testTag("filter_category_all")
                            )
                        }
                        items(ActivityCategory.values()) { category ->
                            FilterChip(
                                selected = selectedCategoryFilter == category,
                                onClick = { selectedCategoryFilter = category },
                                label = { Text(category.displayName) },
                                modifier = Modifier.testTag("filter_category_${category.name}")
                            )
                        }
                    }
                }
            }

            // History List Items
            if (filteredHistory.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No completed activities found for this filter.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            } else {
                items(filteredHistory) { activity ->
                    CompletedActivityItemCard(activity = activity)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    // Avatar Customizer Dialog
    if (showAvatarCustomizer && userProfile != null) {
        AvatarCustomizerDialog(
            initialAvatar = userProfile.avatar,
            currentLevel = userProfile.currentLevel,
            onDismiss = { showAvatarCustomizer = false },
            onSaveAvatar = { newAvatar ->
                onUpdateAvatar(newAvatar)
                showAvatarCustomizer = false
            }
        )
    }

    // OAuth Management Dialog
    if (showOAuthDialog) {
        OAuthAccountDialog(
            userProfile = userProfile,
            onDismiss = { showOAuthDialog = false },
            onUpdateAccount = onUpdateOAuth
        )
    }

    // Badge Detail Dialog
    selectedBadgeForDetail?.let { badge ->
        BadgeDetailDialog(
            badge = badge,
            onDismiss = { selectedBadgeForDetail = null },
            onShareAchievement = onShareAchievement
        )
    }

    // Edit Name Dialog
    if (showEditNameDialog) {
        AlertDialog(
            onDismissRequest = { showEditNameDialog = false },
            title = { Text("Edit Username") },
            text = {
                OutlinedTextField(
                    value = tempName,
                    onValueChange = { tempName = it },
                    label = { Text("Username") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("edit_username_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (tempName.isNotBlank()) {
                            onUpdateUsername(tempName)
                        }
                        showEditNameDialog = false
                    },
                    modifier = Modifier.testTag("confirm_username_button")
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditNameDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileHeroCard(
    userProfile: UserProfile?,
    onCustomizeAvatar: () -> Unit,
    onEditName: () -> Unit,
    onOpenOAuth: () -> Unit
) {
    val profile = userProfile ?: UserProfile()
    val progressFraction = (profile.currentXp.toFloat() / profile.xpForNextLevel.toFloat()).coerceIn(0f, 1f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("profile_hero_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar with customize trigger
                Box(contentAlignment = Alignment.BottomEnd) {
                    AvatarView(
                        avatar = profile.avatar,
                        size = 84.dp,
                        showLevelBadge = true,
                        level = profile.currentLevel
                    )
                    Box(
                        modifier = Modifier
                            .offset(x = 2.dp, y = 2.dp)
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .clickable { onCustomizeAvatar() }
                            .testTag("edit_avatar_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Customize Avatar",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onEditName() }
                    ) {
                        Text(
                            text = profile.username,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit name",
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }

                    // OAuth verification badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(top = 2.dp)
                            .clickable { onOpenOAuth() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = if (profile.isOAuthed) Color(0xFF10B981) else Color.Gray,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (profile.isOAuthed) "Google Account Connected" else "Guest Mode (Tap to link)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (profile.isOAuthed) Color(0xFF047857) else MaterialTheme.colorScheme.outline
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedButton(
                        onClick = onCustomizeAvatar,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("customize_avatar_outlined_button"),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Customize Avatar", fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))

            // Level Progress Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Level ${profile.currentLevel}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Wellness Adventurer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${profile.currentXp} / ${profile.xpForNextLevel} XP",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("level_progress_bar"),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surface
            )
        }
    }
}

@Composable
fun StatsGrid(userProfile: UserProfile?) {
    val profile = userProfile ?: UserProfile()

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Total Points",
            value = "${profile.totalPoints}",
            icon = Icons.Default.Stars,
            iconTint = Color(0xFFF59E0B),
            testTag = "stat_total_points"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Active Streak",
            value = "${profile.currentStreak} Days",
            icon = Icons.Default.LocalFireDepartment,
            iconTint = Color(0xFFEF4444),
            testTag = "stat_active_streak"
        )
        StatCard(
            modifier = Modifier.weight(1f),
            label = "Completed",
            value = "${profile.completedActivitiesCount}",
            icon = Icons.Default.CheckCircle,
            iconTint = Color(0xFF10B981),
            testTag = "stat_completed_count"
        )
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    testTag: String
) {
    Card(
        modifier = modifier.testTag(testTag),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f))
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BadgeCard(badge: AchievementBadge, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .clickable { onClick() }
            .testTag("badge_card_${badge.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
            else
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        border = if (badge.isUnlocked)
            androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFFBBF24))
        else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        if (badge.isUnlocked) Color(0xFFFDE68A) else MaterialTheme.colorScheme.surface
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = badge.iconEmoji, fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = badge.title,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(4.dp))

            LinearProgressIndicator(
                progress = { badge.progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (badge.isUnlocked) Color(0xFF10B981) else MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (badge.isUnlocked) "UNLOCKED ✨" else "${badge.currentValue}/${badge.targetValue}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = if (badge.isUnlocked) Color(0xFF047857) else MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CompletedActivityItemCard(activity: CompletedActivity) {
    val categoryColor = parseColor(activity.category.colorHex, MaterialTheme.colorScheme.primary)
    val formattedDate = remember(activity.completedAt) {
        val sdf = SimpleDateFormat("MMM dd, h:mm a", Locale.getDefault())
        sdf.format(Date(activity.completedAt))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("completed_activity_${activity.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Color Pill
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(categoryColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${activity.category.displayName} • $formattedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (activity.note.isNotBlank()) {
                    Text(
                        text = "\"${activity.note}\"",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF59E0B).copy(alpha = 0.15f)
            ) {
                Text(
                    text = "+${activity.pointsEarned} pts",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFB45309),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
