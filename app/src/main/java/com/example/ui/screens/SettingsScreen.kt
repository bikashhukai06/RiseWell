package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivitySchedule
import com.example.data.model.UserProfile
import com.example.ui.components.OAuthAccountDialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userProfile: UserProfile?,
    schedules: List<ActivitySchedule>,
    isDarkMode: Boolean?,
    notificationsEnabled: Boolean,
    onSetDarkMode: (Boolean?) -> Unit,
    onSetNotifications: (Boolean) -> Unit,
    onUpdateScheduleTime: (id: String, hour: Int, minute: Int, isAlarmEnabled: Boolean) -> Unit,
    onTestNotification: (Context) -> Boolean,
    onUpdateOAuth: (isOAuthed: Boolean, provider: String, email: String, username: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showOAuthDialog by remember { mutableStateOf(false) }
    var scheduleForTimeEdit by remember { mutableStateOf<ActivitySchedule?>(null) }
    var notificationFeedback by remember { mutableStateOf<String?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onSetNotifications(true)
            notificationFeedback = "Push notifications permission granted!"
        } else {
            notificationFeedback = "Notification permission was denied."
        }
    }

    Scaffold(
        modifier = modifier.testTag("settings_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings & Timings",
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Push Notification Banner Feedback
            notificationFeedback?.let { msg ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Info, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = msg, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            // Customizable Activities Timings Section (Crucial prompt requirement)
            item {
                SectionHeader(
                    title = "Customizable Activity Timings",
                    subtitle = "Set your personal alarm clock schedule for each daily habit"
                )
            }

            items(schedules) { schedule ->
                ScheduleTimingCard(
                    schedule = schedule,
                    onEditTime = { scheduleForTimeEdit = schedule },
                    onToggleAlarm = { enabled ->
                        onUpdateScheduleTime(schedule.id, schedule.hour, schedule.minute, enabled)
                    }
                )
            }

            // Push Notifications & Timely Activity Reminders
            item {
                SectionHeader(
                    title = "Push Notifications & Reminders",
                    subtitle = "Timely activity alerts to keep your consistency on track"
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("notification_settings_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Activity Push Notifications",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Receive scheduled alarm chimes and habit prompts",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = notificationsEnabled,
                                onCheckedChange = { checked ->
                                    if (checked && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    } else {
                                        onSetNotifications(checked)
                                    }
                                },
                                modifier = Modifier.testTag("toggle_push_notifications")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                }
                                val sent = onTestNotification(context)
                                notificationFeedback = if (sent) "Alarm notification alert sent to your status bar! ⏰"
                                else "Could not send notification. Please ensure permission is allowed."
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("test_notification_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Alarm Notification Now", fontSize = 13.sp)
                        }
                    }
                }
            }

            // Dark Mode Support
            item {
                SectionHeader(
                    title = "Appearance & Dark Mode",
                    subtitle = "Switch between dark theme, light theme, or follow system"
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("appearance_settings_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = isDarkMode == null,
                                onClick = { onSetDarkMode(null) },
                                label = { Text("System Default") },
                                leadingIcon = {
                                    Icon(Icons.Default.SettingsBrightness, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_system_default")
                            )
                            FilterChip(
                                selected = isDarkMode == true,
                                onClick = { onSetDarkMode(true) },
                                label = { Text("Dark Mode") },
                                leadingIcon = {
                                    Icon(Icons.Default.DarkMode, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_dark_mode")
                            )
                            FilterChip(
                                selected = isDarkMode == false,
                                onClick = { onSetDarkMode(false) },
                                label = { Text("Light") },
                                leadingIcon = {
                                    Icon(Icons.Default.LightMode, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("theme_light_mode")
                            )
                        }
                    }
                }
            }

            // OAuth Authentication & Account Management
            item {
                SectionHeader(
                    title = "OAuth Account Management",
                    subtitle = "Secure authentication, Google OAuth 2.0 integration & profile sync"
                )
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("oauth_settings_card"),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4285F4).copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFF4285F4))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (userProfile?.isOAuthed == true) "Google OAuth Verified" else "Guest Mode Active",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = userProfile?.email ?: "alex.rivers@gmail.com",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedButton(
                            onClick = { showOAuthDialog = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("manage_oauth_button"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.ManageAccounts, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Manage OAuth & Authentication")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }

    // Time Picker Dialog for Activity Timing Customization
    scheduleForTimeEdit?.let { targetSchedule ->
        CustomTimePickerDialog(
            initialHour = targetSchedule.hour,
            initialMinute = targetSchedule.minute,
            activityTitle = targetSchedule.title,
            onDismiss = { scheduleForTimeEdit = null },
            onConfirm = { newHour, newMinute ->
                onUpdateScheduleTime(targetSchedule.id, newHour, newMinute, targetSchedule.isAlarmEnabled)
                scheduleForTimeEdit = null
            }
        )
    }

    if (showOAuthDialog) {
        OAuthAccountDialog(
            userProfile = userProfile,
            onDismiss = { showOAuthDialog = false },
            onUpdateAccount = onUpdateOAuth
        )
    }
}

@Composable
fun ScheduleTimingCard(
    schedule: ActivitySchedule,
    onEditTime: () -> Unit,
    onToggleAlarm: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("timing_card_${schedule.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = schedule.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Days: ${schedule.targetDays} • Category: ${schedule.category.displayName}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Clickable Time Pill
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onEditTime() }
                    .testTag("edit_time_pill_${schedule.id}"),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit timing",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = schedule.formattedTime,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Switch(
                checked = schedule.isAlarmEnabled,
                onCheckedChange = onToggleAlarm,
                modifier = Modifier.testTag("timing_alarm_toggle_${schedule.id}")
            )
        }
    }
}

@Composable
fun CustomTimePickerDialog(
    initialHour: Int,
    initialMinute: Int,
    activityTitle: String,
    onDismiss: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    var hour by remember { mutableStateOf(initialHour) }
    var minute by remember { mutableStateOf(initialMinute) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Set Alarm Timing", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Customize the scheduled time for '$activityTitle'",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Hour selector
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { hour = (hour + 1) % 24 }) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Hour Up")
                        }
                        Text(
                            text = String.format("%02d", hour),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { hour = if (hour == 0) 23 else hour - 1 }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Hour Down")
                        }
                        Text("Hour (24h)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }

                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Minute selector
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(onClick = { minute = (minute + 5) % 60 }) {
                            Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Minute Up")
                        }
                        Text(
                            text = String.format("%02d", minute),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { minute = if (minute < 5) 55 else minute - 5 }) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Minute Down")
                        }
                        Text("Minute", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(hour, minute) },
                modifier = Modifier.testTag("save_timing_button")
            ) {
                Text("Save Time")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SectionHeader(title: String, subtitle: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
