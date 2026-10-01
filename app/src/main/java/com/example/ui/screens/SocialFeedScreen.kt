package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FeedPost
import com.example.data.model.LeaderboardEntry
import com.example.data.model.UserProfile
import com.example.ui.components.AvatarView

enum class SocialSubTab {
    FEED,
    LEADERBOARD
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialFeedScreen(
    userProfile: UserProfile?,
    feedPosts: List<FeedPost>,
    leaderboard: List<LeaderboardEntry>,
    onToggleLike: (String) -> Unit,
    onPublishPost: (activityTitle: String, points: Int, message: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var currentSubTab by remember { mutableStateOf(SocialSubTab.FEED) }
    var showCreatePostDialog by remember { mutableStateOf(false) }
    var newPostMessage by remember { mutableStateOf("") }
    var selectedActivityToShare by remember { mutableStateOf("Rise & Shine Morning Alarm") }

    Scaffold(
        modifier = modifier.testTag("social_feed_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (currentSubTab == SocialSubTab.FEED) "Social Achievement Feed" else "Competitive Leaderboard",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    if (currentSubTab == SocialSubTab.FEED) {
                        FilledTonalButton(
                            onClick = { showCreatePostDialog = true },
                            modifier = Modifier.testTag("open_create_post_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 12.sp)
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Switcher
            PrimaryTabRow(
                selectedTabIndex = currentSubTab.ordinal,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("social_sub_tabs")
            ) {
                Tab(
                    selected = currentSubTab == SocialSubTab.FEED,
                    onClick = { currentSubTab = SocialSubTab.FEED },
                    text = { Text("Friend Feed", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.Feed, contentDescription = null) },
                    modifier = Modifier.testTag("tab_feed")
                )
                Tab(
                    selected = currentSubTab == SocialSubTab.LEADERBOARD,
                    onClick = { currentSubTab = SocialSubTab.LEADERBOARD },
                    text = { Text("Leaderboard", fontWeight = FontWeight.Bold) },
                    icon = { Icon(Icons.Default.EmojiEvents, contentDescription = null) },
                    modifier = Modifier.testTag("tab_leaderboard")
                )
            }

            when (currentSubTab) {
                SocialSubTab.FEED -> {
                    FeedList(
                        posts = feedPosts,
                        onToggleLike = onToggleLike,
                        onOpenShare = { showCreatePostDialog = true }
                    )
                }
                SocialSubTab.LEADERBOARD -> {
                    LeaderboardList(
                        leaderboard = leaderboard,
                        currentUsername = userProfile?.username ?: "Alex Rivers"
                    )
                }
            }
        }
    }

    if (showCreatePostDialog) {
        AlertDialog(
            onDismissRequest = { showCreatePostDialog = false },
            title = { Text("Share Achievement with Friends") },
            text = {
                Column {
                    Text(
                        text = "Broadcast your wellness alarm win or habit milestone to inspire your friends!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = selectedActivityToShare,
                        onValueChange = { selectedActivityToShare = it },
                        label = { Text("Milestone / Activity") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newPostMessage,
                        onValueChange = { newPostMessage = it },
                        label = { Text("Share your message") },
                        placeholder = { Text("e.g. Woke up on the first alarm bell! 12-day streak locked in 🔥") },
                        maxLines = 3,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("new_post_message_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newPostMessage.isNotBlank()) {
                            onPublishPost(selectedActivityToShare, 50, newPostMessage)
                            newPostMessage = ""
                        }
                        showCreatePostDialog = false
                    },
                    modifier = Modifier.testTag("submit_new_post_button")
                ) {
                    Text("Publish Post")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreatePostDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FeedList(
    posts: List<FeedPost>,
    onToggleLike: (String) -> Unit,
    onOpenShare: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Share Bar
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenShare() }
                    .testTag("quick_share_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Share an alarm victory or streak with friends...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.weight(1f)
                    )
                    FilledIconButton(
                        onClick = onOpenShare,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Post", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        items(posts) { post ->
            FeedPostCard(post = post, onToggleLike = { onToggleLike(post.id) })
        }
    }
}

@Composable
fun FeedPostCard(
    post: FeedPost,
    onToggleLike: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("feed_post_${post.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Author row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarView(
                    avatar = post.authorAvatar,
                    size = 44.dp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.authorName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = post.timeAgo,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+${post.pointsEarned} pts",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Activity Badge Tag
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = post.activityTitle,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Post content message
            Text(
                text = post.message,
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
            Spacer(modifier = Modifier.height(8.dp))

            // Actions (Like & Comments)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { onToggleLike() }
                        .padding(vertical = 4.dp, horizontal = 8.dp)
                        .testTag("like_post_button_${post.id}")
                ) {
                    Icon(
                        imageVector = if (post.hasLiked) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (post.hasLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likesCount} Cheers",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (post.hasLiked) FontWeight.Bold else FontWeight.Normal,
                        color = if (post.hasLiked) Color(0xFFEF4444) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.commentsCount} comments",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardList(
    leaderboard: List<LeaderboardEntry>,
    currentUsername: String
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Podium for Top 3
        item {
            PodiumCard(leaderboard = leaderboard.take(3))
        }

        item {
            Text(
                text = "Rankings (Weekly Diamond Tier)",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )
        }

        items(leaderboard) { entry ->
            val isUser = entry.isCurrentUser || entry.username.equals(currentUsername, ignoreCase = true)
            LeaderboardRow(entry = entry, isUser = isUser)
        }
    }
}

@Composable
fun PodiumCard(leaderboard: List<LeaderboardEntry>) {
    if (leaderboard.size < 3) return

    val first = leaderboard[0]
    val second = leaderboard[1]
    val third = leaderboard[2]

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("podium_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🏆 Weekly Champions Podium",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place
                PodiumColumn(
                    entry = second,
                    rank = 2,
                    podiumHeight = 85.dp,
                    color = Color(0xFF94A3B8)
                )

                // 1st Place (Center, Tallest)
                PodiumColumn(
                    entry = first,
                    rank = 1,
                    podiumHeight = 110.dp,
                    color = Color(0xFFF59E0B)
                )

                // 3rd Place
                PodiumColumn(
                    entry = third,
                    rank = 3,
                    podiumHeight = 70.dp,
                    color = Color(0xFFD97706)
                )
            }
        }
    }
}

@Composable
fun PodiumColumn(
    entry: LeaderboardEntry,
    rank: Int,
    podiumHeight: androidx.compose.ui.unit.Dp,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        AvatarView(avatar = entry.avatar, size = if (rank == 1) 54.dp else 44.dp)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = entry.username.split(" ").first(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
        Text(
            text = "${entry.points} pts",
            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pedestal
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(podiumHeight),
            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
            color = color.copy(alpha = 0.3f),
            border = androidx.compose.foundation.BorderStroke(1.5.dp, color)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = color
                )
            }
        }
    }
}

@Composable
fun LeaderboardRow(entry: LeaderboardEntry, isUser: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_${entry.rank}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isUser) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
            else MaterialTheme.colorScheme.surface
        ),
        border = if (isUser) androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Rank Number
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        when (entry.rank) {
                            1 -> Color(0xFFFBBF24)
                            2 -> Color(0xFFE2E8F0)
                            3 -> Color(0xFFFED7AA)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${entry.rank}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = if (entry.rank == 1) Color(0xFF78350F) else MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            AvatarView(avatar = entry.avatar, size = 42.dp)

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isUser) "${entry.username} (You)" else entry.username,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isUser) FontWeight.ExtraBold else FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Level ${entry.level} • ${entry.streak}-day streak 🔥",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF59E0B).copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${entry.points} pts",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD97706),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}
